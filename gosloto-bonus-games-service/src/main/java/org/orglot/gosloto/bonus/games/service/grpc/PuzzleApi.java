package org.orglot.gosloto.bonus.games.service.grpc;

import com.google.protobuf.Int64Value;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.orglot.bonus.games.model.GrpcMapper;
import org.orglot.bonus.games.model.puzzle.Puzzle;
import org.orglot.bonus.games.model.puzzle.PuzzleRequest;
import org.orglot.gosloto.bonus.games.exception.ApiGrpcBonusGameException;
import org.orglot.gosloto.bonus.games.exception.GrpcBonusGameExceptionHandler;
import org.orglot.gosloto.bonus.games.grpc.PuzzleGrpc;
import org.orglot.gosloto.bonus.games.grpc.PuzzleRequestGrpc;
import org.orglot.gosloto.bonus.games.grpc.PuzzleSearchRequestGrpc;
import org.orglot.gosloto.bonus.games.grpc.ReactorPuzzleServiceGrpc;
import org.orglot.gosloto.bonus.games.grpc.UpdatePuzzleRequestGrpc;
import org.orglot.gosloto.bonus.games.grpc.model.NullableInt;
import org.orglot.gosloto.bonus.games.grpc.model.NullableLong;
import org.orglot.gosloto.bonus.games.service.puzzle.collection.PuzzleService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class PuzzleApi extends ReactorPuzzleServiceGrpc.PuzzleServiceImplBase {

  private final PuzzleService puzzleService;

  @Override
  public Flux<PuzzleGrpc> search(PuzzleSearchRequestGrpc request) {
    return puzzleService.search(
            request.getOffset(),
            request.getLimit(),
            request.getSearchString().isEmpty() ? null : request.getSearchString(),
            request.getRarity().isEmpty() ? null : request.getRarity()
        ).map(GrpcMapper::toPuzzleGrpc)
        .onErrorResume(ex -> Mono.error(new ApiGrpcBonusGameException(ex, PuzzleApi.class.getName(), "search")));
  }

  @Override
  public Mono<PuzzleGrpc> getPuzzle(Int64Value request) {
    return puzzleService.getPuzzle(request.getValue())
        .map(GrpcMapper::toPuzzleGrpc)
        .defaultIfEmpty(PuzzleGrpc.getDefaultInstance());
  }

  @Override
  public Mono<NullableLong> save(PuzzleRequestGrpc request) {
    PuzzleRequest dto = new PuzzleRequest(
        request.getName(),
        request.getType(),
        request.getGameType().getValue(),
        request.getRarity(),
        request.getUrl(),
        request.getPrize()
    );
    return puzzleService.save(dto)
        .map(id -> NullableLong.newBuilder().setValue(id).build());
  }

  @Override
  public Mono<NullableInt> update(UpdatePuzzleRequestGrpc request) {
    PuzzleRequest dto = new PuzzleRequest(
        request.getPayload().getName(),
        request.getPayload().getType(),
        request.getPayload().getGameType().getValue(),
        request.getPayload().getRarity(),
        request.getPayload().getUrl(),
        request.getPayload().getPrize()
    );
    return puzzleService.update(request.getPuzzleId(), dto)
        .map(result -> NullableInt.newBuilder().setValue(result).build())
        .onErrorResume(ex ->
            Mono.error(new ApiGrpcBonusGameException(ex, PuzzleApi.class.getName(), "update")));
  }

  @Override
  public Mono<NullableLong> updateById(PuzzleGrpc request) {
    Puzzle domain = GrpcMapper.toPuzzle(request);
    return puzzleService.updatePuzzleById(domain)
        .map(id -> NullableLong.newBuilder().setValue(id).build())
        .onErrorResume(ex ->
            Mono.error(new ApiGrpcBonusGameException(ex, PuzzleApi.class.getName(), "updateById")));
  }

  @Override
  protected Throwable onErrorMap(Throwable throwable) {
    log.error("handled in puzzle handler - {} ", throwable.getMessage(), throwable);
    if (throwable instanceof ApiGrpcBonusGameException grpcBonusGameException) {
      return GrpcBonusGameExceptionHandler.forException(grpcBonusGameException);
    }
    return GrpcBonusGameExceptionHandler.forException(throwable);
  }

}
