package org.orglot.gosloto.bonus.games.client.operations.main;

import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;
import org.orglot.bonus.games.model.GrpcMapper;
import org.orglot.bonus.games.model.puzzle.PuzzleItem;
import org.orglot.bonus.games.model.puzzle.PuzzleItemRequest;
import org.orglot.gosloto.bonus.games.grpc.GetByIdRequestGrpc;
import org.orglot.gosloto.bonus.games.grpc.PuzzleItemGrpc;
import org.orglot.gosloto.bonus.games.grpc.ReactorPuzzleItemServiceGrpc;
import org.orglot.gosloto.bonus.games.grpc.SavePuzzleItemsRequestGrpc;
import org.orglot.gosloto.bonus.games.grpc.UpdatePuzzleItemByIdRequestGrpc;
import org.orglot.gosloto.bonus.games.grpc.UpdatePuzzleItemsRequestGrpc;
import org.orglot.gosloto.bonus.games.grpc.model.NullableLong;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
public class PuzzleItemsOperationsImpl implements PuzzleItemsOperations {

  private final ReactorPuzzleItemServiceGrpc.ReactorPuzzleItemServiceStub puzzleItemService;

  public PuzzleItemsOperationsImpl(ManagedChannel channel) {
    this.puzzleItemService = ReactorPuzzleItemServiceGrpc.newReactorStub(channel);
  }

  @Override
  public Mono<Void> savePuzzleItems(Long puzzleId, List<PuzzleItemRequest> items) {
    SavePuzzleItemsRequestGrpc req = GrpcMapper.toSavePuzzleItemsRequestGrpc(puzzleId, items);
    return puzzleItemService.save(req)
        .then()
        .doOnError(e -> log.error("Error in savePuzzleItems: {}", e.getMessage()));
  }

  @Override
  public Mono<Long> create(PuzzleItem item) {
    PuzzleItemGrpc req = GrpcMapper.toPuzzleItemGrpc(item);
    return puzzleItemService.create(req)
        .map(res -> res.hasValue() ? res.getValue() : null)
        .doOnError(e -> log.error("Error in create PuzzleItem: {}", e.getMessage()));
  }

  @Override
  public Flux<PuzzleItem> getItems(Long puzzleId) {
    NullableLong request = puzzleId != null ?
        NullableLong.newBuilder().setValue(puzzleId).build() :
        NullableLong.newBuilder().build();
    return puzzleItemService.getItems(request)
        .map(GrpcMapper::toPuzzleItem)
        .doOnError(e -> log.error("Error in getItems: {}", e.getMessage()));
  }

  @Override
  public Mono<Long> updatePuzzleItems(Long puzzleId, List<PuzzleItemRequest> items) {
    UpdatePuzzleItemsRequestGrpc req = GrpcMapper.toUpdatePuzzleItemsRequestGrpc(puzzleId, items);
    return puzzleItemService.update(req)
        .map(res -> res.hasValue() ? res.getValue() : null)
        .doOnError(e -> log.error("Error in updatePuzzleItems: {}", e.getMessage()));
  }

  @Override
  public Mono<Long> updateById(PuzzleItem item) {
    UpdatePuzzleItemByIdRequestGrpc req = GrpcMapper.toUpdatePuzzleItemByIdRequestGrpc(item);
    return puzzleItemService.updateById(req)
        .map(res -> res.hasValue() ? res.getValue() : null)
        .doOnError(e -> log.error("Error in updateById PuzzleItem: {}", e.getMessage()));
  }

  @Override
  public Mono<PuzzleItem> getById(Long id) {
    if (id == null) {
      return Mono.error(new IllegalArgumentException("PuzzleItem id cannot be null"));
    }
    GetByIdRequestGrpc req = GetByIdRequestGrpc.newBuilder()
        .setId(id)
        .build();
    return puzzleItemService.getById(req)
        .map(GrpcMapper::toPuzzleItem)
        .doOnError(e -> log.error("Error in getById PuzzleItem: {}", e.getMessage(), e));
  }

}
