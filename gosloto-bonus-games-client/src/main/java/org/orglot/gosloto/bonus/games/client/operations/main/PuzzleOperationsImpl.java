package org.orglot.gosloto.bonus.games.client.operations.main;

import com.google.protobuf.Int64Value;
import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;
import org.orglot.bonus.games.model.GrpcMapper;
import org.orglot.bonus.games.model.puzzle.Puzzle;
import org.orglot.bonus.games.model.puzzle.PuzzleRequest;
import org.orglot.gosloto.bonus.games.grpc.PuzzleSearchRequestGrpc;
import org.orglot.gosloto.bonus.games.grpc.ReactorPuzzleServiceGrpc;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class PuzzleOperationsImpl implements PuzzleOperations {

  private final ReactorPuzzleServiceGrpc.ReactorPuzzleServiceStub puzzleService;

  public PuzzleOperationsImpl(ManagedChannel channel) {
    this.puzzleService = ReactorPuzzleServiceGrpc.newReactorStub(channel);
  }

  @Override
  public Flux<Puzzle> search(int offset, int limit, String searchString, String rarity) {
    PuzzleSearchRequestGrpc.Builder request = PuzzleSearchRequestGrpc.newBuilder()
        .setOffset(offset)
        .setLimit(limit);

    if (searchString != null) request.setSearchString(searchString);
    if (rarity != null) request.setRarity(rarity);

    return puzzleService.search(request.build())
        .map(GrpcMapper::toPuzzle)
        .doOnError(e -> log.error("Error in search puzzle: {}", e.getMessage(), e));
  }

  @Override
  public Mono<Puzzle> getPuzzle(Long puzzleId) {
    return puzzleService.getPuzzle(Int64Value.of(puzzleId))
        .map(GrpcMapper::toPuzzle)
        .doOnError(e -> log.error("Error in getPuzzle: {}", e.getMessage(), e));
  }

  @Override
  public Mono<Long> save(PuzzleRequest request) {
    return puzzleService.save(GrpcMapper.toPuzzleRequestGrpc(request))
        .map(res -> res.hasValue() ? res.getValue() : null)
        .doOnError(e -> log.error("Error in save puzzle: {}", e.getMessage()));
  }

  @Override
  public Mono<Integer> update(Long puzzleId, PuzzleRequest request) {
    return puzzleService.update(GrpcMapper.toUpdatePuzzleRequestGrpc(puzzleId, request))
        .map(res -> res.hasValue() ? res.getValue() : null)
        .doOnError(e -> log.error("Error in update puzzle: {}", e.getMessage()));
  }

  @Override
  public Mono<Long> updatePuzzleById(Puzzle puzzle) {
    return puzzleService.updateById(GrpcMapper.toPuzzleGrpc(puzzle))
        .map(res -> res.hasValue() ? res.getValue() : null)
        .doOnError(e -> log.error("Error in updatePuzzleById: {}", e.getMessage()));
  }

}

