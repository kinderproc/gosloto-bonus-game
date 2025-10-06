package org.orglot.gosloto.bonus.games.service.grpc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.orglot.gosloto.bonus.games.exception.ApiGrpcBonusGameException;
import org.orglot.gosloto.bonus.games.exception.GrpcBonusGameExceptionHandler;
import org.orglot.gosloto.bonus.games.grpc.model.NullableIntegerList;
import org.orglot.gosloto.bonus.games.grpc.model.RewardsGrpc;
import org.orglot.gosloto.bonus.games.service.RewardService;
import org.orglot.gosloto.bonus.games.service.grpc.mapper.GrpcMapper;
import org.orglot.gosloto.reward.grpc.ApplyRewardRequestGrpc;
import org.orglot.gosloto.reward.grpc.GetAndApplyRewardsRequestGrpc;
import org.orglot.gosloto.reward.grpc.ReactorGrpcRewardServiceGrpc;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class RewardApi extends ReactorGrpcRewardServiceGrpc.GrpcRewardServiceImplBase {

  private final RewardService rewardService;

  @Override
  public Mono<RewardsGrpc> getRandomRewardsByRandomIds(NullableIntegerList request) {
    return rewardService.getRandomRewardsByRandomIds(GrpcMapper.toIntegerList(request))
        .collectList()
        .map(GrpcMapper::toRewardsGrpc)
        .switchIfEmpty(Mono.just(RewardsGrpc.getDefaultInstance()))
        .onErrorResume(ex -> Mono.error(new ApiGrpcBonusGameException(ex, RewardApi.class.getName(), "getRandomRewardsByRandomIds")))
        .doOnError(ex -> log.error(ex.getMessage(), ex));
  }

  @Override
  public Mono<RewardsGrpc> applyReward(ApplyRewardRequestGrpc request) {
    return rewardService.apply(GrpcMapper.fromRewardsGrpc(request.getRewards()), GrpcMapper.toApplyRewardUserData(request.getUserData()))
        .collectList()
        .map(GrpcMapper::toRewardsGrpc)
        .switchIfEmpty(Mono.just(RewardsGrpc.getDefaultInstance()))
        .onErrorResume(ex -> Mono.error(new ApiGrpcBonusGameException(ex, RewardApi.class.getName(), "applyReward")))
        .doOnError(ex -> log.error(ex.getMessage(), ex));
  }

  @Override
  public Mono<RewardsGrpc> getAndApplyRewards(GetAndApplyRewardsRequestGrpc request) {
    return rewardService.getAndApplyRewards(GrpcMapper.toIntegerList(request.getRandomIds()),
            GrpcMapper.toApplyRewardUserData(request.getUserData()))
        .collectList()
        .map(GrpcMapper::toRewardsGrpc)
        .switchIfEmpty(Mono.just(RewardsGrpc.getDefaultInstance()))
        .onErrorResume(ex -> Mono.error(new ApiGrpcBonusGameException(ex, RewardApi.class.getName(), "getAndApplyRewards")))
        .doOnError(ex -> log.error(ex.getMessage(), ex));
  }

  @Override
  protected Throwable onErrorMap(Throwable throwable) {
    log.error("handled in reward handler - {} ", throwable.getMessage(), throwable);
    if (throwable instanceof ApiGrpcBonusGameException grpcBonusGameException) {
      return GrpcBonusGameExceptionHandler.forException(grpcBonusGameException);
    }
    return GrpcBonusGameExceptionHandler.forException(throwable);
  }
}
