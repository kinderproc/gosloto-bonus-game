package org.orglot.gosloto.bonus.games.service.grpc;

import com.google.protobuf.Empty;
import com.google.protobuf.Int64Value;
import com.google.protobuf.NullValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.orglot.bonus.games.model.response.Reward;
import org.orglot.gosloto.bonus.games.exception.ApiGrpcBonusGameException;
import org.orglot.gosloto.bonus.games.exception.GrpcBonusGameExceptionHandler;
import org.orglot.gosloto.bonus.games.grpc.model.NullableBoolean;
import org.orglot.gosloto.bonus.games.grpc.model.NullableLong;
import org.orglot.gosloto.bonus.games.service.RewardService;
import org.orglot.gosloto.bonus.games.service.grpc.mapper.GrpcMapper;
import org.orglot.gosloto.bonus.games.service.puzzle.collection.PuzzleCollectionService;
import org.orglot.gosloto.bonus.games.service.puzzle.collection.RarityService;
import org.orglot.gosloto.reward.grpc.CollectPuzzleAndRefillBonusRequestGrpc;
import org.orglot.gosloto.reward.grpc.Puzzle;
import org.orglot.gosloto.reward.grpc.RandomPuzzleRequest;
import org.orglot.gosloto.reward.grpc.RarityGrpc;
import org.orglot.gosloto.reward.grpc.ReactorGrpcPuzzleCollectionServiceGrpc;
import org.orglot.gosloto.reward.grpc.UserPuzzleWithCollectedCountGrpc;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class PuzzleCollectionApi extends ReactorGrpcPuzzleCollectionServiceGrpc.GrpcPuzzleCollectionServiceImplBase {
    private final RewardService rewardService;
    private final PuzzleCollectionService puzzleCollectionService;
    private final RarityService rarityService;

    @Override
    public Flux<Puzzle> randomPuzzle(RandomPuzzleRequest request) {
        return rewardService.applyPuzzleItemReward(
                Reward.builder()
                        .value(request.getCount())
                        .id(request.getPuzzleId())
                        .build(), request.getUserId()
        ).map(reward -> {
            NullableLong.Builder value = NullableLong.newBuilder();
            if (reward.getId() != null) {
                value.setValue(reward.getId());
            } else {
                value.setNull(NullValue.NULL_VALUE);
            }
            Puzzle.Builder builder = Puzzle.newBuilder()
                    .setPuzzleId(value.build())
                    .setUrl(reward.getUrl())
                    .setDescription(reward.getDescription());
            return builder.build();
        });
    }

    @Override
    public Mono<Empty> collectPuzzleAndRefillBonusToUser(CollectPuzzleAndRefillBonusRequestGrpc request) {
        return puzzleCollectionService.collectAndRefillBonusTransactional(request.getUserId(),
                        request.getPuzzleId(), request.getMobile())
                .map(r -> Empty.getDefaultInstance())
                .defaultIfEmpty(Empty.getDefaultInstance())
                .onErrorResume(ex ->
                        Mono.error(new ApiGrpcBonusGameException(ex, PuzzleCollectionApi.class.getName(),
                                "collectPuzzleAndRefillBonusToUser")));
    }

    @Override
    public Flux<UserPuzzleWithCollectedCountGrpc> getUserCollectionGroupingByType(Int64Value request) {
        return puzzleCollectionService.getUserCollectionGroupingByType(request.getValue())
                .map(GrpcMapper::toUserPuzzleWithCollectedCountGrpc)
                .onErrorResume(ex ->
                        Mono.error(new ApiGrpcBonusGameException(ex, PuzzleCollectionApi.class.getName(),
                                "getUserCollectionGroupingByType")));
    }

    @Override
    public Flux<RarityGrpc> getRarities(NullableBoolean request) {
        return rarityService.findAllByDisplay(request.hasValue() ? request.getValue() : null)
                .map(GrpcMapper::toRarityGrpc)
                .onErrorResume(ex ->
                        Mono.error(new ApiGrpcBonusGameException(ex, PuzzleCollectionApi.class.getName(),
                                "getRaritiesDictionaryFull")));
    }

    @Override
    protected Throwable onErrorMap(Throwable throwable) {
        log.error("handled in collections of puzzles handler - {} ", throwable.getMessage(), throwable);
        if (throwable instanceof ApiGrpcBonusGameException grpcBonusGameException) {
            return GrpcBonusGameExceptionHandler.forException(grpcBonusGameException);
        }
        return GrpcBonusGameExceptionHandler.forException(throwable);
    }
}
