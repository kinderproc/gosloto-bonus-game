package org.orglot.gosloto.bonus.games.client.operations.main;

import org.orglot.bonus.games.model.request.ApplyRewardUserData;
import org.orglot.bonus.games.model.response.Reward;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Клиент для взаимодействия с функционалом рандомизации и наград
 */
public interface RewardOperations {

  /**
   * Получение рандомной награды для каждого идентификатора рандома (ссылка на таблицу с вероятностями выпадения наград)
   *
   * @param randomIds список идентификаторов рандома наград
   * @return список рандомных наград
   */
  Flux<Reward> getRandomRewardsByRandomIds(List<Integer> randomIds);

  /**
   * Применение наград пользователю
   *
   * @param rewards  список наград, которые необходимо применить пользователю
   * @param userData данные пользователя, необходимые для применения наград ему
   * @return список примененных наград
   */
  Flux<Reward> applyReward(List<Reward> rewards, ApplyRewardUserData userData);

  /**
   * Получение рандомной награды для каждого идентификатора рандома (ссылка на таблицу с вероятностями выпадения наград)
   * и применение этих наград пользователю
   *
   * @param randomIds список идентификаторов рандома наград
   * @param userData  данные пользователя, необходимые для применения наград ему
   * @return список примененных наград
   */
  Flux<Reward> getAndApplyRewards(List<Integer> randomIds, ApplyRewardUserData userData);
}
