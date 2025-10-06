package org.orglot.gosloto.bonus.games.client.operations.main;

import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;
import org.orglot.bonus.games.model.GrpcMapper;
import org.orglot.bonus.games.model.request.ApplyRewardUserData;
import org.orglot.bonus.games.model.response.Reward;
import org.orglot.gosloto.reward.grpc.ReactorGrpcRewardServiceGrpc;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
public class RewardOperationsImpl implements RewardOperations {

  private final ReactorGrpcRewardServiceGrpc.ReactorGrpcRewardServiceStub rewardService;

  public RewardOperationsImpl(ManagedChannel channel) {
    this.rewardService = ReactorGrpcRewardServiceGrpc.newReactorStub(channel);
  }

  /**
   * Получение рандомной награды для каждого идентификатора рандома (ссылка на таблицу с вероятностями выпадения наград)
   *
   * @param randomIds список идентификаторов рандома наград
   * @return список рандомных наград
   */
  @Override
  public Flux<Reward> getRandomRewardsByRandomIds(List<Integer> randomIds) {
    return rewardService.getRandomRewardsByRandomIds(GrpcMapper.toNullableIntegerList(randomIds))
        .map(GrpcMapper::toRewards)
        .flatMapMany(Flux::fromIterable)
        .onErrorResume(error -> {
          log.error("Error when getRandomRewardsByRandomIds: {}", error.getMessage());
          return Flux.empty();
        });
  }

  /**
   * Применение наград пользователю
   *
   * @param rewards  список наград, которые необходимо применить пользователю
   * @param userData данные пользователя, необходимые для применения наград ему
   * @return список примененных наград
   */
  @Override
  public Flux<Reward> applyReward(List<Reward> rewards, ApplyRewardUserData userData) {
    return rewardService.applyReward(GrpcMapper.toApplyRewardRequestGrpc(rewards, userData))
        .map(GrpcMapper::toRewards)
        .flatMapMany(Flux::fromIterable)
        .onErrorResume(error -> {
          log.error("Error when applyReward: {}", error.getMessage());
          return Flux.empty();
        });
  }

  /**
   * Получение рандомной награды для каждого идентификатора рандома (ссылка на таблицу с вероятностями выпадения наград)
   * и применение этих наград пользователю
   *
   * @param randomIds список идентификаторов рандома наград
   * @param userData  данные пользователя, необходимые для применения наград ему
   * @return список примененных наград
   */
  @Override
  public Flux<Reward> getAndApplyRewards(List<Integer> randomIds, ApplyRewardUserData userData) {
    return rewardService.getAndApplyRewards(GrpcMapper.toGetAndApplyRewardsRequestGrpc(randomIds, userData))
        .map(GrpcMapper::toRewards)
        .flatMapMany(Flux::fromIterable)
        .onErrorResume(error -> {
          log.error("Error when getAndApplyRewards: {}", error.getMessage());
          return Flux.empty();
        });
  }
}
