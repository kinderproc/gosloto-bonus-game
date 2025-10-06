package org.orglot.gosloto.bonus.games.client.operations.main;

import com.google.protobuf.Empty;
import com.google.protobuf.NullValue;
import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;
import org.orglot.bonus.games.model.GrpcMapper;
import org.orglot.bonus.games.model.request.BonusGameBuy;
import org.orglot.bonus.games.model.request.BonusGameEnd;
import org.orglot.bonus.games.model.request.BonusGamePlay;
import org.orglot.bonus.games.model.response.AvailableBonusGames;
import org.orglot.bonus.games.model.response.BonusGameBuyStatus;
import org.orglot.bonus.games.model.response.BonusGameCollection;
import org.orglot.bonus.games.model.response.BonusGameConfigOrLastSession;
import org.orglot.bonus.games.model.response.BonusGamePlayStatus;
import org.orglot.bonus.games.model.response.BonusGameTransferStatus;
import org.orglot.bonus.games.model.response.GameCompleteResult;
import org.orglot.gosloto.bonus.games.grpc.AvailableBonusGamesResponse;
import org.orglot.gosloto.bonus.games.grpc.BonusGamePlayRequest;
import org.orglot.gosloto.bonus.games.grpc.GetBonusGameConfigRequest;
import org.orglot.gosloto.bonus.games.grpc.GetUserCollectionRequest;
import org.orglot.gosloto.bonus.games.grpc.ReactorGrpcBonusGameServiceGrpc;
import org.orglot.gosloto.bonus.games.grpc.model.NullableLong;
import org.orglot.gosloto.bonus.games.grpc.model.NullableString;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.validation.constraints.NotNull;

@Slf4j
public class BonusGameOperationsImpl implements BonusGameOperations {

  private final ReactorGrpcBonusGameServiceGrpc.ReactorGrpcBonusGameServiceStub bonusGameService;

  public BonusGameOperationsImpl(ManagedChannel channel) {
    this.bonusGameService = ReactorGrpcBonusGameServiceGrpc.newReactorStub(channel);
  }

  @Override
  public Flux<String> getBonusGames() {
    return bonusGameService.getBonusGames(Empty.getDefaultInstance())
        .map(StringValue::getValue)
        .doOnError(error -> log.error("Error when getAvailableBonusGames: {}", error.getMessage()));
  }

  /**
   * Получение доступных бонусных игр
   *
   * @return список с наименованиями игр и свойствами видимости
   */
  @Override
  @Deprecated
  public Mono<AvailableBonusGames> getAvailableBonusGames() {
    return bonusGameService.getAvailableBonusGames(Empty.getDefaultInstance())
        .map(AvailableBonusGamesResponse::getGamesList)
        .flatMapIterable(list -> list)
        .map(GrpcMapper::toGameTypeResponse)
        .collectList()
        .map(games -> AvailableBonusGames.builder().games(games).build())
        .defaultIfEmpty(AvailableBonusGames.empty())
        .doOnError(error -> log.error("Error when getAvailableBonusGames: {}", error.getMessage()));
  }

  /**
   * Получение активной сессии пользователя по типу игры,
   * в случае ее отсутствия - всех возможных ставок игры (режимов, доступных пазлов и т.п.)
   *
   * @param gameType - название игры
   * @param userId   - идентификатор пользователя
   * @return UUID активной сессии или возможные ставки игры (конфигурации)
   */
  @Override
  public Mono<BonusGameConfigOrLastSession> getBonusGameConfig(Long userId, @NotNull String gameType) {
    return bonusGameService.getBonusGameConfig(GetBonusGameConfigRequest.newBuilder()
            .setGameType(gameType)
            .setUserId(Objects.isNull(userId) ? NullableLong.newBuilder().setNull(NullValue.NULL_VALUE).build() :
                NullableLong.newBuilder().setValue(userId).build())
            .build())
        .map(GrpcMapper::toBonusGameConfigOrLastSession)
        .defaultIfEmpty(BonusGameConfigOrLastSession.empty())
        .doOnError(error -> log.error("Error when getBonusGameConfig: {}", error.getMessage()));
  }

  /**
   * Покупка игры пользователя (валидация данных, списание бонусов пользователя через SAP, создание сессии игры - в случае успеха)
   *
   * @param gameBuy - информация для покупки бонусной игры пользователем
   * @return информация по статусу покупки (при успешном списании - uuid сессии, иначе информация об ошибке)
   */
  @Override
  public Mono<BonusGameBuyStatus> buy(BonusGameBuy gameBuy) {
    return bonusGameService.buy(GrpcMapper.toBonusGameBuyRequest(gameBuy))
        .map(GrpcMapper::toBonusGameBuyStatus)
        .defaultIfEmpty(BonusGameBuyStatus.empty())
        .doOnError(error -> log.error("Error when buy: {}", error.getMessage()));
  }

  /**
   * Статус покупки игры (состояние оплаты)
   *
   * @param sessionUUID - UUID сессии игры
   * @param userId      - идентификатор пользователя
   * @return информация по статусу покупки (при успешном списании - uuid сессии, иначе информация об ошибке)
   */
  @Override
  public Mono<BonusGameBuyStatus> buyStatus(UUID sessionUUID, long userId) {
    return bonusGameService.buyStatus(GrpcMapper.toBonusGameBuyStatusRequest(sessionUUID, userId))
        .map(GrpcMapper::toBonusGameBuyStatus)
        .defaultIfEmpty(BonusGameBuyStatus.empty())
        .doOnError(error -> log.error("Error when buyStatus: {}", error.getMessage()));
  }

  /**
   * Запуск игры пользователя (реализации отличаются в зависимости от категории игры)
   *
   * @param gamePlay - информация для запуска игры
   * @return результаты запуска игры, либо информация об ошибке при запуске
   */
  @Override
  public Mono<BonusGamePlayStatus> play(BonusGamePlay gamePlay) {
    return bonusGameService.play(GrpcMapper.toBonusGamePlayRequest(gamePlay))
        .map(GrpcMapper::toBonusGamePlayStatus)
        .defaultIfEmpty(BonusGamePlayStatus.empty())
        .doOnError(error -> log.error("Error when play: {}", error.getMessage()));
  }

  /**
   * Статус запуска игры (состояние сессии) (реализации отличаются в зависимости от категории игры)
   *
   * @param sessionUUID UUID сессии игры
   * @param userId      идентификатор пользователя
   * @return информация по статусу запуска игры (результаты запуска игры, либо информация об ошибке при запуске)
   */
  @Override
  public Mono<BonusGamePlayStatus> playStatus(UUID sessionUUID, long userId) {
    return bonusGameService.playStatus(BonusGamePlayRequest.newBuilder().setUserId(userId).setUuid(sessionUUID.toString()).build())
        .map(GrpcMapper::toBonusGamePlayStatus)
        .defaultIfEmpty(BonusGamePlayStatus.empty())
        .doOnError(error -> log.error("Error when playStatus: {}", error.getMessage()));
  }

  /**
   * Получение списка элементов пазла, полученных пользователем
   *
   * @param userId идентификатор пользователя
   * @param rarityType тип редкости пазла
   * @return список элементов пазлов
   */
  @Override
  public Mono<List<BonusGameCollection>> getUserCollection(long userId, String rarityType) {
    return bonusGameService.getUserCollection(GetUserCollectionRequest.newBuilder()
            .setRarityType(Objects.nonNull(rarityType) ?
                NullableString.newBuilder().setValue(rarityType).build() :
                NullableString.newBuilder().setNull(NullValue.NULL_VALUE).build()
            )
            .setUserId(userId)
            .build())
        .map(GrpcMapper::toBonusGameCollectionList)
        .defaultIfEmpty(Collections.emptyList())
        .doOnError(error -> log.error("Error when getUserCollection: {}", error.getMessage()));
  }

  /**
   * Завершение сессии по бонусной игре пользователем (реализации отличаются в зависимости от категории игры)
   *
   * @param bonusGameEnd
   * @return - null в случае ошибки при завершении, в противном случае - информация о завершении
   */
  @Override
  public Mono<GameCompleteResult> bonusGameEnd(BonusGameEnd bonusGameEnd) {
    return bonusGameService.complete(GrpcMapper.toBonusGameCompleteRequest(bonusGameEnd))
        .map(GrpcMapper::toGameCompleteResult)
        .defaultIfEmpty(GameCompleteResult.empty())
        .doOnError(error -> log.error("Error when bonusGameEnd: {}", error.getMessage()));
  }

  /**
   * @param sessionUUID сессия пользователя
   * @param userId      идентификатор пользователя
   * @return статус покупки, в случае статуса ERROR информация об ошибке
   * @deprecated Статус выплаты
   */
  @Deprecated
  @Override
  public Mono<BonusGameTransferStatus> transferStatus(UUID sessionUUID, long userId) {
    return bonusGameService.transferStatus(BonusGamePlayRequest.newBuilder().setUuid(sessionUUID.toString()).setUserId(userId).build())
        .map(GrpcMapper::toBonusGameTransferStatus)
        .defaultIfEmpty(BonusGameTransferStatus.empty())
        .doOnError(error -> log.error("Error when transferStatus: {}", error.getMessage()));
  }

}
