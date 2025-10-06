package org.orglot.gosloto.bonus.games.service.grpc;

import com.google.protobuf.Empty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.orglot.bonus.games.model.GrpcMapper;
import org.orglot.bonus.games.model.puzzle.PuzzleItem;
import org.orglot.bonus.games.model.puzzle.PuzzleItemRequest;
import org.orglot.gosloto.bonus.games.exception.ApiGrpcBonusGameException;
import org.orglot.gosloto.bonus.games.exception.GrpcBonusGameExceptionHandler;
import org.orglot.gosloto.bonus.games.grpc.GetByIdRequestGrpc;
import org.orglot.gosloto.bonus.games.grpc.PuzzleItemGrpc;
import org.orglot.gosloto.bonus.games.grpc.ReactorPuzzleItemServiceGrpc;
import org.orglot.gosloto.bonus.games.grpc.SavePuzzleItemsRequestGrpc;
import org.orglot.gosloto.bonus.games.grpc.UpdatePuzzleItemByIdRequestGrpc;
import org.orglot.gosloto.bonus.games.grpc.UpdatePuzzleItemsRequestGrpc;
import org.orglot.gosloto.bonus.games.grpc.model.NullableLong;
import org.orglot.gosloto.bonus.games.service.puzzle.collection.PuzzleItemService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PuzzleItemsApi extends ReactorPuzzleItemServiceGrpc.PuzzleItemServiceImplBase {

  private final PuzzleItemService puzzleItemService;

  @Override
  public Mono<Empty> save(SavePuzzleItemsRequestGrpc request) {
    List<PuzzleItemRequest> dto = request.getItemsList().stream()
        .map(i -> new PuzzleItemRequest(i.getPuzzleItemId(), i.getUrl()))
        .collect(Collectors.toList());
    return puzzleItemService.save(request.getPuzzleId(), dto)
        .then(Mono.just(Empty.getDefaultInstance()))
        .onErrorResume(ex ->
            Mono.error(new ApiGrpcBonusGameException(ex, PuzzleItemsApi.class.getName(), "save")));
  }

  @Override
  public Mono<NullableLong> create(PuzzleItemGrpc request) {
    PuzzleItem item = GrpcMapper.toPuzzleItem(request);
    return puzzleItemService.create(item)
        .map(id -> NullableLong.newBuilder().setValue(id).build())
        .onErrorResume(ex ->
            Mono.error(new ApiGrpcBonusGameException(ex, PuzzleItemsApi.class.getName(), "create")));
  }

  @Override
  public Flux<PuzzleItemGrpc> getItems(NullableLong request) {
    return puzzleItemService.getItems(request.getValue())
        .map(GrpcMapper::toPuzzleItemGrpc)
        .onErrorResume(ex ->
            Flux.error(new ApiGrpcBonusGameException(ex, PuzzleItemsApi.class.getName(), "getItems")));
  }

  @Override
  public Mono<NullableLong> update(UpdatePuzzleItemsRequestGrpc request) {
    List<PuzzleItemRequest> dto = request.getItemsList().stream()
        .map(i -> new PuzzleItemRequest(i.getPuzzleItemId(), i.getUrl()))
        .collect(Collectors.toList());
    return puzzleItemService.update(request.getPuzzleId(), dto)
        .map(result -> NullableLong.newBuilder().setValue(result).build())
        .onErrorResume(ex ->
            Mono.error(new ApiGrpcBonusGameException(ex, PuzzleItemsApi.class.getName(), "update")));
  }

  @Override
  public Mono<NullableLong> updateById(UpdatePuzzleItemByIdRequestGrpc request) {
    PuzzleItem it = GrpcMapper.toPuzzleItem(request.getItem());
    return puzzleItemService.updateById(it)
        .map(id -> NullableLong.newBuilder().setValue(id).build())
        .onErrorResume(ex ->
            Mono.error(new ApiGrpcBonusGameException(ex, PuzzleItemsApi.class.getName(), "updateById")));
  }

  @Override
  public Mono<PuzzleItemGrpc> getById(GetByIdRequestGrpc request) {
    return puzzleItemService.getById(request.getId())
        .map(GrpcMapper::toPuzzleItemGrpc)
        .onErrorResume(ex ->
            Mono.error(new ApiGrpcBonusGameException(ex, PuzzleItemsApi.class.getName(), "getById")));
  }

  @Override
  protected Throwable onErrorMap(Throwable throwable) {
    log.error("handled in puzzle item handler - {} ", throwable.getMessage(), throwable);
    if (throwable instanceof ApiGrpcBonusGameException grpcBonusGameException) {
      return GrpcBonusGameExceptionHandler.forException(grpcBonusGameException);
    }
    return GrpcBonusGameExceptionHandler.forException(throwable);
  }

}
