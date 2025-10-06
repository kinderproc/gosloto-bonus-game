package org.orglot.gosloto.bonus.games.service.grpc;

import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.orglot.bonus.games.model.BonusGameType;
import org.orglot.gosloto.bonus.games.exception.ApiGrpcBonusGameException;
import org.orglot.gosloto.bonus.games.exception.GrpcBonusGameExceptionHandler;
import org.orglot.gosloto.bonus.games.grpc.AvailableBonusGamesResponse;
import org.orglot.gosloto.bonus.games.grpc.BonusGameBuyRequest;
import org.orglot.gosloto.bonus.games.grpc.BonusGameBuyStatusGrpc;
import org.orglot.gosloto.bonus.games.grpc.BonusGameBuyStatusRequest;
import org.orglot.gosloto.bonus.games.grpc.BonusGameCompleteRequest;
import org.orglot.gosloto.bonus.games.grpc.BonusGameConfigOrLastSessionResponse;
import org.orglot.gosloto.bonus.games.grpc.BonusGamePlayRequest;
import org.orglot.gosloto.bonus.games.grpc.BonusGamePlayStatusGrpc;
import org.orglot.gosloto.bonus.games.grpc.BonusGameTransferStatusGrpc;
import org.orglot.gosloto.bonus.games.grpc.GameCompleteResultGrpc;
import org.orglot.gosloto.bonus.games.grpc.GetBonusGameConfigRequest;
import org.orglot.gosloto.bonus.games.grpc.GetUserCollectionRequest;
import org.orglot.gosloto.bonus.games.grpc.GetUserCollectionResponse;
import org.orglot.gosloto.bonus.games.grpc.ReactorGrpcBonusGameServiceGrpc;
import org.orglot.gosloto.bonus.games.service.ReactiveBonusGameServiceImpl;
import org.orglot.gosloto.bonus.games.service.grpc.mapper.GrpcMapper;
import org.orglot.gosloto.bonus.games.service.puzzle.collection.PuzzleCollectionService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BonusGameApi extends ReactorGrpcBonusGameServiceGrpc.GrpcBonusGameServiceImplBase {

    private final ReactiveBonusGameServiceImpl reactiveBonusGameServiceImpl;
    private final PuzzleCollectionService puzzleCollectionService;

    @Override
    public Mono<AvailableBonusGamesResponse> getAvailableBonusGames(Empty request) {
        return reactiveBonusGameServiceImpl.getAvailableBonusGames()
                .collectList()
                .map(games -> AvailableBonusGamesResponse.newBuilder().addAllGames(games).build())
                .switchIfEmpty(Mono.just(AvailableBonusGamesResponse.getDefaultInstance()))
                .onErrorResume(ex -> Mono.error(new ApiGrpcBonusGameException(ex, BonusGameApi.class.getName(), "getActiveBonusGames")))
                .doOnError(ex -> log.error(ex.getMessage(), ex));
    }

    @Override
    public Flux<StringValue> getBonusGames(Empty request) {
        return Flux.fromIterable(BonusGameType.getStringValues().stream().map(StringValue::of).toList());
    }

    @Override
    public Mono<BonusGameConfigOrLastSessionResponse> getBonusGameConfig(GetBonusGameConfigRequest request) {
        return reactiveBonusGameServiceImpl.getBonusGameConfigOrSessionUUID(request.getGameType(), request.getUserId().hasValue() ?
                request.getUserId().getValue() : null)
                .map(GrpcMapper::toBonusGameConfigOrUserSessionResponse)
                .switchIfEmpty(Mono.just(BonusGameConfigOrLastSessionResponse.getDefaultInstance()))
                .onErrorResume(ex -> Mono.error(new ApiGrpcBonusGameException(ex, BonusGameApi.class.getName(), "getBonusGameConfig")))
                .doOnError(ex -> log.error(ex.getMessage(), ex));
    }

    @Override
    public Mono<BonusGameBuyStatusGrpc> buy(BonusGameBuyRequest request) {
        return reactiveBonusGameServiceImpl.buyGame(GrpcMapper.toBonusGameBuy(request))
                .map(GrpcMapper::toBonusGameBuyStatusGrpc)
                .switchIfEmpty(Mono.just(BonusGameBuyStatusGrpc.getDefaultInstance()))
                .onErrorResume(ex -> Mono.error(new ApiGrpcBonusGameException(ex, BonusGameApi.class.getName(), "buy")))
                .doOnError(ex -> log.error(ex.getMessage(), ex));
    }

    @Override
    public Mono<BonusGameBuyStatusGrpc> buyStatus(BonusGameBuyStatusRequest request) {
        return reactiveBonusGameServiceImpl.buyStatus(UUID.fromString(request.getUuid()), request.getUserId())
                .map(GrpcMapper::toBonusGameBuyStatusGrpc)
                .switchIfEmpty(Mono.just(BonusGameBuyStatusGrpc.getDefaultInstance()))
                .onErrorResume(ex -> Mono.error(new ApiGrpcBonusGameException(ex, BonusGameApi.class.getName(), "buyStatus")))
                .doOnError(ex -> log.error(ex.getMessage(), ex));
    }

    @Override
    public Mono<BonusGamePlayStatusGrpc> play(BonusGamePlayRequest request) {
        return reactiveBonusGameServiceImpl.play(UUID.fromString(request.getUuid()), request.getUserId(),
                        request.getMode().hasValue() ? request.getMode().getValue() : null)
                .map(GrpcMapper::toBonusGamePlayStatusGrpc)
                .switchIfEmpty(Mono.just(BonusGamePlayStatusGrpc.getDefaultInstance()))
                .onErrorResume(ex -> Mono.error(new ApiGrpcBonusGameException(ex, BonusGameApi.class.getName(), "play")))
                .doOnError(ex -> log.error(ex.getMessage(), ex));
    }

    @Override
    public Mono<BonusGamePlayStatusGrpc> playStatus(BonusGamePlayRequest request) {
        return reactiveBonusGameServiceImpl.playStatus(UUID.fromString(request.getUuid()), request.getUserId())
                .map(GrpcMapper::toBonusGamePlayStatusGrpc)
                .switchIfEmpty(Mono.just(BonusGamePlayStatusGrpc.getDefaultInstance()))
                .onErrorResume(ex -> Mono.error(new ApiGrpcBonusGameException(ex, BonusGameApi.class.getName(), "playStatus")))
                .doOnError(ex -> log.error(ex.getMessage(), ex));
    }

    @Override
    public Mono<GameCompleteResultGrpc> complete(BonusGameCompleteRequest request) {
        return reactiveBonusGameServiceImpl.completeBonusGame(UUID.fromString(request.getUuid()), request.getUserId(),
                        request.getIsWin().hasValue() ? request.getIsWin().getValue() : null,
                        request.getScore(),
                        request.getAvscore(),
                        request.getMobile())
                .map(GrpcMapper::toGameCompleteResultGrpc)
                .switchIfEmpty(Mono.just(GameCompleteResultGrpc.getDefaultInstance()))
                .onErrorResume(ex -> Mono.error(new ApiGrpcBonusGameException(ex, BonusGameApi.class.getName(), "complete")))
                .doOnError(ex -> log.error(ex.getMessage(), ex));
    }

    @Override
    public Mono<GetUserCollectionResponse> getUserCollection(GetUserCollectionRequest request) {
        return puzzleCollectionService.getUserCollection(request.getUserId(),
                request.getRarityType().hasValue() ? request.getRarityType().getValue() : null)
                .map(GrpcMapper::toGetUserCollectionResponse)
                .switchIfEmpty(Mono.just(GetUserCollectionResponse.getDefaultInstance()))
                .onErrorResume(ex -> Mono.error(new ApiGrpcBonusGameException(ex, BonusGameApi.class.getName(), "getUserCollection")))
                .doOnError(ex -> log.error(ex.getMessage(), ex));
    }

    @Deprecated
    @Override
    public Mono<BonusGameTransferStatusGrpc> transferStatus(BonusGamePlayRequest request) {
        return reactiveBonusGameServiceImpl.transferStatus(UUID.fromString(request.getUuid()), request.getUserId())
                .map(GrpcMapper::toBonusGameTransferStatusGrpc)
                .switchIfEmpty(Mono.just(BonusGameTransferStatusGrpc.getDefaultInstance()))
                .onErrorResume(ex -> Mono.error(new ApiGrpcBonusGameException(ex, BonusGameApi.class.getName(), "transferStatus")))
                .doOnError(ex -> log.error(ex.getMessage(), ex));
    }

    @Override
    protected Throwable onErrorMap(Throwable throwable) {
        log.error("handled in global handler - {} ", throwable.getMessage(), throwable);
        if (throwable instanceof ApiGrpcBonusGameException grpcBonusGameException) {
            return GrpcBonusGameExceptionHandler.forException(grpcBonusGameException);
        }
        return GrpcBonusGameExceptionHandler.forException(throwable);
    }
}
