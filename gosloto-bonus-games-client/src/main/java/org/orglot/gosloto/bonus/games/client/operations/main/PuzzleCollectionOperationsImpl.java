package org.orglot.gosloto.bonus.games.client.operations.main;

import com.google.protobuf.Int64Value;
import com.google.protobuf.NullValue;
import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;
import org.orglot.bonus.games.model.GrpcMapper;
import org.orglot.bonus.games.model.puzzle.PuzzleModel;
import org.orglot.bonus.games.model.response.Rarity;
import org.orglot.bonus.games.model.response.UserPuzzleWithCollectedCount;
import org.orglot.gosloto.bonus.games.grpc.model.NullableBoolean;
import org.orglot.gosloto.reward.grpc.CollectPuzzleAndRefillBonusRequestGrpc;
import org.orglot.gosloto.reward.grpc.RandomPuzzleRequest;
import org.orglot.gosloto.reward.grpc.ReactorGrpcPuzzleCollectionServiceGrpc;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
public class PuzzleCollectionOperationsImpl implements PuzzleCollectionOperations {

    private final ReactorGrpcPuzzleCollectionServiceGrpc.ReactorGrpcPuzzleCollectionServiceStub puzzleCollectionService;

    public PuzzleCollectionOperationsImpl(ManagedChannel channel) {
        this.puzzleCollectionService = ReactorGrpcPuzzleCollectionServiceGrpc.newReactorStub(channel);
    }

    @Override
    public Mono<Void> collectPuzzleAndRefillBonusToUser(long userId, long puzzleId, String mobile) {
        return puzzleCollectionService.collectPuzzleAndRefillBonusToUser(CollectPuzzleAndRefillBonusRequestGrpc.newBuilder()
                        .setUserId(userId)
                        .setPuzzleId(puzzleId)
                        .setMobile(mobile)
                        .build())
                .then();
    }

    @Override
    public Flux<Rarity> getRaritiesByDisplay(Boolean display) {
        return puzzleCollectionService.getRarities(Objects.nonNull(display) ?
                        NullableBoolean.newBuilder().setValue(display).build() :
                        NullableBoolean.newBuilder().setNull(NullValue.NULL_VALUE).build())
                .map(item -> new Rarity(item.getName(), item.getTitle(), item.getOrder()));
    }

    @Override
    public Flux<UserPuzzleWithCollectedCount> getUserCollectionGroupingByType(long userId) {
        return puzzleCollectionService.getUserCollectionGroupingByType(Int64Value.newBuilder().setValue(userId).build())
                .map(GrpcMapper::toUserPuzzleWithCollectedCount);
    }

    @Override
    public Flux<PuzzleModel> randomPuzzles(long userId, Long puzzleId, int count) {
        return puzzleCollectionService.randomPuzzle(
                RandomPuzzleRequest.newBuilder().setPuzzleId(puzzleId).setUserId(userId).setCount(count).build()
        ).map(GrpcMapper::toPuzzleModel);
    }
}
