package org.orglot.bonus.games.model;

import com.google.protobuf.NullValue;
import com.google.protobuf.Timestamp;
import lombok.experimental.UtilityClass;
import org.orglot.bonus.games.model.puzzle.Puzzle;
import org.orglot.bonus.games.model.puzzle.PuzzleItem;
import org.orglot.bonus.games.model.puzzle.PuzzleItemRequest;
import org.orglot.bonus.games.model.puzzle.PuzzleModel;
import org.orglot.bonus.games.model.puzzle.PuzzleRequest;
import org.orglot.bonus.games.model.request.ApplyRewardUserData;
import org.orglot.bonus.games.model.request.BonusGameBuy;
import org.orglot.bonus.games.model.request.BonusGameBuyCoupon;
import org.orglot.bonus.games.model.request.BonusGameBuyData;
import org.orglot.bonus.games.model.request.BonusGameEnd;
import org.orglot.bonus.games.model.request.BonusGamePlay;
import org.orglot.bonus.games.model.response.BonusGameBuyStatus;
import org.orglot.bonus.games.model.response.BonusGameCollection;
import org.orglot.bonus.games.model.response.BonusGameCollectionItem;
import org.orglot.bonus.games.model.response.BonusGameConfigOrLastSession;
import org.orglot.bonus.games.model.response.BonusGamePlayStatus;
import org.orglot.bonus.games.model.response.BonusGameTransferStatus;
import org.orglot.bonus.games.model.response.Consumable;
import org.orglot.bonus.games.model.response.GameCompleteResult;
import org.orglot.bonus.games.model.response.GameModeResponse;
import org.orglot.bonus.games.model.response.GameTypeResponse;
import org.orglot.bonus.games.model.response.LastSession;
import org.orglot.bonus.games.model.response.LotteryDrawInfo;
import org.orglot.bonus.games.model.response.LotteryTicket;
import org.orglot.bonus.games.model.response.LotteryTicketData;
import org.orglot.bonus.games.model.response.LotteryTicketDataCombination;
import org.orglot.bonus.games.model.response.PlayStatusResponse;
import org.orglot.bonus.games.model.response.PriceAndScaleOfMode;
import org.orglot.bonus.games.model.response.PrizeType;
import org.orglot.bonus.games.model.response.PurchaseStatus;
import org.orglot.bonus.games.model.response.PuzzleRarityAndCollectedCount;
import org.orglot.bonus.games.model.response.Reward;
import org.orglot.bonus.games.model.response.UserPuzzleWithCollectedCount;
import org.orglot.bonus.games.model.response.config.ModeTypeGameConfig;
import org.orglot.gosloto.bonus.games.grpc.BonusGameBuyCouponRequest;
import org.orglot.gosloto.bonus.games.grpc.BonusGameBuyDataRequest;
import org.orglot.gosloto.bonus.games.grpc.BonusGameBuyRequest;
import org.orglot.gosloto.bonus.games.grpc.BonusGameBuyStatusGrpc;
import org.orglot.gosloto.bonus.games.grpc.BonusGameBuyStatusRequest;
import org.orglot.gosloto.bonus.games.grpc.BonusGameCompleteRequest;
import org.orglot.gosloto.bonus.games.grpc.BonusGameConfigOrLastSessionResponse;
import org.orglot.gosloto.bonus.games.grpc.BonusGamePlayRequest;
import org.orglot.gosloto.bonus.games.grpc.BonusGamePlayStatusGrpc;
import org.orglot.gosloto.bonus.games.grpc.BonusGameTransferStatusGrpc;
import org.orglot.gosloto.bonus.games.grpc.ConsumableGrpc;
import org.orglot.gosloto.bonus.games.grpc.DrawInfoGrpc;
import org.orglot.gosloto.bonus.games.grpc.GameCompleteResultGrpc;
import org.orglot.gosloto.bonus.games.grpc.GameModeResponseGrpc;
import org.orglot.gosloto.bonus.games.grpc.GameTypeResponseGrpc;
import org.orglot.gosloto.bonus.games.grpc.GetUserCollectionResponse;
import org.orglot.gosloto.bonus.games.grpc.NullableBonusGameBuyDataRequest;
import org.orglot.gosloto.bonus.games.grpc.NullableGameConfig;
import org.orglot.gosloto.bonus.games.grpc.NullableTicketDataCombinationsGrpc;
import org.orglot.gosloto.bonus.games.grpc.NullableTicketsGrpc;
import org.orglot.gosloto.bonus.games.grpc.PriceAndScaleOfModeGrpc;
import org.orglot.gosloto.bonus.games.grpc.PuzzleGrpc;
import org.orglot.gosloto.bonus.games.grpc.PuzzleItemGrpc;
import org.orglot.gosloto.bonus.games.grpc.PuzzleItemRequestGrpc;
import org.orglot.gosloto.bonus.games.grpc.PuzzleRequestGrpc;
import org.orglot.gosloto.bonus.games.grpc.SavePuzzleItemsRequestGrpc;
import org.orglot.gosloto.bonus.games.grpc.TicketDataCombinationsGrpc;
import org.orglot.gosloto.bonus.games.grpc.TicketDataGrpc;
import org.orglot.gosloto.bonus.games.grpc.TicketGrpc;
import org.orglot.gosloto.bonus.games.grpc.TicketsGrpc;
import org.orglot.gosloto.bonus.games.grpc.UpdatePuzzleItemByIdRequestGrpc;
import org.orglot.gosloto.bonus.games.grpc.UpdatePuzzleItemsRequestGrpc;
import org.orglot.gosloto.bonus.games.grpc.UpdatePuzzleRequestGrpc;
import org.orglot.gosloto.bonus.games.grpc.model.IntegerList;
import org.orglot.gosloto.bonus.games.grpc.model.NullableBoolean;
import org.orglot.gosloto.bonus.games.grpc.model.NullableInt;
import org.orglot.gosloto.bonus.games.grpc.model.NullableInteger;
import org.orglot.gosloto.bonus.games.grpc.model.NullableIntegerList;
import org.orglot.gosloto.bonus.games.grpc.model.NullableLong;
import org.orglot.gosloto.bonus.games.grpc.model.NullableString;
import org.orglot.gosloto.bonus.games.grpc.model.RewardGrpc;
import org.orglot.gosloto.bonus.games.grpc.model.RewardsGrpc;
import org.orglot.gosloto.reward.grpc.ApplyRewardRequestGrpc;
import org.orglot.gosloto.reward.grpc.ApplyRewardUserDataGrpc;
import org.orglot.gosloto.reward.grpc.GetAndApplyRewardsRequestGrpc;
import org.orglot.gosloto.reward.grpc.UserPuzzleWithCollectedCountGrpc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@UtilityClass
public class GrpcMapper {
  public GameTypeResponse toGameTypeResponse(GameTypeResponseGrpc game) {
    return GameTypeResponse.builder()
        .gameType(game.getGameType())
        .visible(game.getVisible())
        .smallIcon(game.getSmallIcon().hasValue() ? game.getSmallIcon().getValue() : null)
        .bigIcon(game.getBigIcon().hasValue() ? game.getBigIcon().getValue() : null)
        .smallHover(game.getSmallHover().hasValue() ? game.getSmallHover().getValue() : null)
        .gameUrl(game.getGameUrl().hasValue() ? game.getGameUrl().getValue() : null)
        .mpIcon(game.getMpIcon().hasValue() ? game.getMpIcon().getValue() : null)
        .rules(game.getRules().hasValue() ? game.getRules().getValue() : null)
        .title(game.getTitle().hasValue() ? game.getTitle().getValue() : null)
        .order(game.getOrder())
        .gameSpec(game.getGameSpec())
        .prize(game.getPrize().hasValue() ? game.getPrize().getValue() : null)
        .currency(game.getCurrency().hasValue() ? game.getCurrency().getValue() : null)
        .puzzle(game.getPuzzle().hasValue() ? game.getPuzzle().getValue() : null)
        .puzzleColor(game.getPuzzleColor().hasValue() ? game.getPuzzleColor().getValue() : null)
        .price(game.getPrice().hasValue() ? game.getPrice().getValue() : null)
        .build();
  }

  public BonusGameConfigOrLastSession toBonusGameConfigOrLastSession(BonusGameConfigOrLastSessionResponse response) {
    return BonusGameConfigOrLastSession.builder()
        .sessionUUID(response.getSessionUUID().hasValue() ? UUID.fromString(response.getSessionUUID().getValue()) : null)
        .gameConfig(GrpcMapper.toGameConfig(response.getGameConfig()))
        .lastSession(response.getLastSession().hasValue() ? LastSession.builder()
            .price(response.getLastSession().getValue().getPrice().hasValue() ?
                response.getLastSession().getValue().getPrice().getValue() : null)
            .modeNumber(response.getLastSession().getValue().getModeNumber().hasValue() ?
                response.getLastSession().getValue().getModeNumber().getValue() : null)
            .sessionUUID(UUID.fromString(response.getLastSession().getValue().getSessionUUID()))
            .prizes(response.getLastSession().getValue().getPrizes().hasData() ?
                toIntegerList(response.getLastSession().getValue().getPrizes().getData()) : null)
            .scale(response.getLastSession().getValue().getScaleMap())
                .consumables(response.getLastSession().getValue().getConsumablesList().stream()
                        .map(GrpcMapper::toConsumable)
                        .toList())
            .build() : null)
        .build();
  }

  public List<Integer> toIntegerList(IntegerList integerList) {
    return integerList.getDataList().stream()
        .filter(NullableInteger::hasValue)
        .map(NullableInteger::getValue)
        .collect(Collectors.toList());
  }

  public ModeTypeGameConfig toGameConfig(NullableGameConfig gameConfig) {
    if (gameConfig.hasValue()) {
      return new ModeTypeGameConfig(toModeList(gameConfig.getValue().getModesList()), gameConfig.getValue().getGameType());
    }
    return null;
  }

  public List<GameModeResponse> toModeList(List<GameModeResponseGrpc> modes) {
    return modes.stream()
        .map(mode -> GameModeResponse.builder()
            .modeNumber(mode.getModeNumber())
            .priceAndScale(toPriceAndScaleOfModeList(mode.getPriceAndScaleList()))
            .description(mode.getDescription().hasValue() ? mode.getDescription().getValue() : null)
            .build())
        .collect(Collectors.toList());
  }

  private List<PriceAndScaleOfMode> toPriceAndScaleOfModeList(List<PriceAndScaleOfModeGrpc> priceAndScaleOfModeGrpcList) {
    return priceAndScaleOfModeGrpcList.stream()
        .map(priceAndScaleOfModeGrpc -> PriceAndScaleOfMode.builder()
            .price(priceAndScaleOfModeGrpc.getPrice())
            .scale(priceAndScaleOfModeGrpc.getScaleMap())
            .build()
        )
        .collect(Collectors.toList());
  }

  public BonusGameBuyRequest toBonusGameBuyRequest(BonusGameBuy gameBuy) {
    BonusGameBuyRequest.Builder builder = BonusGameBuyRequest.newBuilder()
        .setGameType(gameBuy.getGameType())
        .setMobile(gameBuy.getMobile())
        .setUserId(gameBuy.getUserId())
        .setMode(Objects.nonNull(gameBuy.getMode()) ? NullableInt.newBuilder().setValue(gameBuy.getMode()).build() :
            NullableInt.newBuilder().setNull(NullValue.NULL_VALUE).build())
        .setPrice(Objects.nonNull(gameBuy.getPrice()) ? NullableInt.newBuilder().setValue(gameBuy.getPrice()).build() :
            NullableInt.newBuilder().setNull(NullValue.NULL_VALUE).build());
    if (Objects.nonNull(gameBuy.getDevicePlatform())) {
      builder.setDevicePlatform(gameBuy.getDevicePlatform());
    }
    BonusGameBuyData gameData = gameBuy.getGameData();
    builder.setGameData(Objects.nonNull(gameData) ?
        NullableBonusGameBuyDataRequest.newBuilder().setValue(toBonusGameBuyDataRequest(gameData)).build() :
        NullableBonusGameBuyDataRequest.newBuilder().setNull(NullValue.NULL_VALUE).build()
    );
    return builder.build();
  }

  public BonusGameBuyStatus toBonusGameBuyStatus(BonusGameBuyStatusGrpc bonusGameBuyStatusGrpc) {
    return BonusGameBuyStatus.builder()
        .error(bonusGameBuyStatusGrpc.getError().hasValue() ? bonusGameBuyStatusGrpc.getError().getValue() : null)
        .status(PurchaseStatus.valueOf(bonusGameBuyStatusGrpc.getStatus().name()))
        .sessionUUID(bonusGameBuyStatusGrpc.getSessionUUID().hasValue() ?
            UUID.fromString(bonusGameBuyStatusGrpc.getSessionUUID().getValue()) : null)
        .build();
  }

  public BonusGameBuyStatusRequest toBonusGameBuyStatusRequest(UUID sessionUUID, long userId) {
    return BonusGameBuyStatusRequest.newBuilder()
        .setUserId(userId)
        .setUuid(sessionUUID.toString())
        .build();
  }

  public BonusGamePlayRequest toBonusGamePlayRequest(BonusGamePlay gamePlay) {
    return BonusGamePlayRequest.newBuilder()
        .setUuid(gamePlay.getSessionUUID().toString())
        .setUserId(gamePlay.getUserId())
        .setMode(Objects.nonNull(gamePlay.getModeNumber()) ? NullableInt.newBuilder().setValue(gamePlay.getModeNumber()).build() :
            NullableInt.newBuilder().setNull(NullValue.NULL_VALUE).build())
        .build();
  }

  public BonusGamePlayStatus toBonusGamePlayStatus(BonusGamePlayStatusGrpc bonusGamePlayStatusGrpc) {
    return BonusGamePlayStatus.builder()
        .error(bonusGamePlayStatusGrpc.getError().hasValue() ? bonusGamePlayStatusGrpc.getError().getValue() : null)
        .prizes(bonusGamePlayStatusGrpc.getPrizes().hasData() ? toIntegerList(bonusGamePlayStatusGrpc.getPrizes().getData()) : null)
        .sessionUUID(bonusGamePlayStatusGrpc.getSessionUUID().hasValue() ?
            UUID.fromString(bonusGamePlayStatusGrpc.getSessionUUID().getValue()) : null)
        .scale(bonusGamePlayStatusGrpc.getScaleMap())
        .status(PlayStatusResponse.valueOf(bonusGamePlayStatusGrpc.getStatus().name()))
        .consumables(bonusGamePlayStatusGrpc.getConsumablesList().stream()
                .map(GrpcMapper::toConsumable)
                .toList())
        .build();
  }

  private static Consumable toConsumable(ConsumableGrpc c) {
    return Consumable.builder()
            .id(c.hasId() ? c.getId().getValue() : -1)
            .name(c.hasName() ? c.getName().getValue() : "")
            .price(c.hasPrice() ? c.getPrice().getValue() : -1)
            .available(c.getAvailable())
            .weight(c.hasWeight() ? c.getWeight().getValue() : -1)
            .iconUrl(c.hasIconUrl() ? c.getIconUrl().getValue() : "")
            .build();
  }

  public static List<BonusGameCollection> toBonusGameCollectionList(GetUserCollectionResponse response) {
    if (response.getGetUserCollectionElementsCount() > 0) {
      return response.getGetUserCollectionElementsList().stream()
          .map(element -> BonusGameCollection.builder()
              .gameType(element.getGameType())
              .type(element.getType())
              .status(element.getStatus())
              .prize(element.getPrize())
              .collected(element.getCollected())
              .name(element.getName())
              .rarity(element.getRarity())
              .createDate(timestampToDate(element.getCreateDate()))
              .puzzleID(element.getPuzzleID())
              .puzzleURL(element.getPuzzleURL())
              .puzzleItems(element.getPuzzleItemsList().stream()
                  .map(item -> BonusGameCollectionItem.builder()
                      .id(item.getId())
                      .url(item.getUrl())
                      .puzzleItemId(item.getPuzzleItemId())
                      .build())
                  .collect(Collectors.toList()))
              .build())
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  public static Instant timestampToDate(Timestamp timestamp) {
    if (Timestamp.getDefaultInstance().equals(timestamp)) {
      return null;
    }
    return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
  }

  public static BonusGameCompleteRequest toBonusGameCompleteRequest(BonusGameEnd bonusGameEnd) {
    return BonusGameCompleteRequest.newBuilder()
        .setUuid(bonusGameEnd.getSessionUUID().toString())
        .setUserId(bonusGameEnd.getUserId())
        .setIsWin(Objects.nonNull(bonusGameEnd.getIsWin()) ?
            NullableBoolean.newBuilder().setValue(bonusGameEnd.getIsWin()).build() :
            NullableBoolean.newBuilder().setNull(NullValue.NULL_VALUE).build())
        .setMobile(bonusGameEnd.getMobile())
        .setAvscore(bonusGameEnd.getAvscore())
        .setScore(bonusGameEnd.getScore())
        .build();
  }

  public static GameCompleteResult toGameCompleteResult(GameCompleteResultGrpc completeResultGrpc) {
    return GameCompleteResult.builder()
        .totalPrize(completeResultGrpc.getTotalPrize())
        .rewards(completeResultGrpc.getRewards().hasRewards() ?
            toRewardList(completeResultGrpc.getRewards().getRewards().getRewardList()) : null)
        .status(PlayStatusResponse.valueOf(completeResultGrpc.getStatus().name()))
        .error(completeResultGrpc.getError().hasValue() ? completeResultGrpc.getError().getValue() : null)
        .tickets(toLotteryTicketList(completeResultGrpc.getTickets()))
        .build();
  }

  public static List<Reward> toRewardList(List<RewardGrpc> rewardGrpc) {
    if (Objects.isNull(rewardGrpc)) {
      return Collections.emptyList();
    }
    return rewardGrpc.stream()
        .map(GrpcMapper::toReward)
        .collect(Collectors.toList());
  }

  public static BonusGameTransferStatus toBonusGameTransferStatus(BonusGameTransferStatusGrpc bonusGameTransferStatusGrpc) {
    return BonusGameTransferStatus.builder()
        .status(PurchaseStatus.valueOf(bonusGameTransferStatusGrpc.getStatus().name()))
        .error(bonusGameTransferStatusGrpc.getError().hasValue() ? bonusGameTransferStatusGrpc.getError().getValue() : null)
        .build();
  }

  public NullableIntegerList toNullableIntegerList(List<Integer> values) {
    if (Objects.isNull(values) || values.isEmpty()) {
      return NullableIntegerList.newBuilder().setNull(NullValue.NULL_VALUE).build();
    }
    List<NullableInteger> result = values.stream()
        .map(integer -> NullableInteger.newBuilder().setValue(integer).build())
        .collect(Collectors.toList());
    return NullableIntegerList.newBuilder().setData(IntegerList.newBuilder().addAllData(result).build()).build();
  }

  public List<Reward> toRewards(RewardsGrpc rewardsGrpc) {
    return toRewardList(rewardsGrpc.getRewardList());
  }

  public ApplyRewardRequestGrpc toApplyRewardRequestGrpc(List<Reward> rewards, ApplyRewardUserData userData) {
    return ApplyRewardRequestGrpc.newBuilder()
        .setRewards(toRewardsGrpc(rewards))
        .setUserData(toApplyRewardUserDataGrpc(userData))
        .build();
  }

  public static RewardsGrpc toRewardsGrpc(List<Reward> rewards) {
    if (Objects.isNull(rewards) || rewards.isEmpty()) {
      return RewardsGrpc.getDefaultInstance();
    }
    List<RewardGrpc> grpcRewards = rewards.stream()
        .map(rd -> {
          RewardGrpc.Builder r = RewardGrpc.newBuilder();
          if (Objects.nonNull(rd.getId())) {
            r.setId(NullableLong.newBuilder().setValue(rd.getId()).build());
          } else {
            r.setId(NullableLong.getDefaultInstance());
          }
          if (Objects.nonNull(rd.getUrl())) {
            r.setUrl(NullableString.newBuilder().setValue(rd.getUrl()).build());
          } else {
            r.setUrl(NullableString.getDefaultInstance());
          }
          if (Objects.nonNull(rd.getValue())) {
            r.setValue(NullableInt.newBuilder().setValue(rd.getValue()).build());
          } else {
            r.setValue(NullableInt.getDefaultInstance());
          }
          if (Objects.nonNull(rd.getPrizeSubType())) {
            r.setPrizeSubType(NullableString.newBuilder().setValue(rd.getPrizeSubType().name()).build());
          } else {
            r.setPrizeSubType(NullableString.getDefaultInstance());
          }
          if (Objects.nonNull(rd.getType())) {
            r.setType(NullableString.newBuilder().setValue(rd.getType().name()).build());
          } else {
            r.setType(NullableString.getDefaultInstance());
          }
          return r.build();
        }).collect(Collectors.toList());

    return RewardsGrpc.newBuilder().addAllReward(grpcRewards).build();
  }

  public ApplyRewardUserDataGrpc toApplyRewardUserDataGrpc(ApplyRewardUserData userData) {
    ApplyRewardUserDataGrpc.Builder userDataGrpc = ApplyRewardUserDataGrpc.newBuilder().setUserId(userData.getUserId());
    if (Objects.nonNull(userData.getMobile())) {
      userDataGrpc.setMobile(NullableString.newBuilder().setValue(userData.getMobile()).build());
    } else {
      userDataGrpc.setMobile(NullableString.getDefaultInstance());
    }
    if (Objects.nonNull(userData.getBonusGameSessionUUID())) {
      userDataGrpc.setBonusGameSessionUUID(NullableString.newBuilder()
          .setValue(userData.getBonusGameSessionUUID().toString()).build());
    } else {
      userDataGrpc.setBonusGameSessionUUID(NullableString.getDefaultInstance());
    }
    if (Objects.nonNull(userData.getRefillBonusDescription())) {
      userDataGrpc.setRefillBonusDescription(NullableString.newBuilder()
          .setValue(userData.getRefillBonusDescription()).build());
    } else {
      userDataGrpc.setRefillBonusDescription(NullableString.getDefaultInstance());
    }
    if (Objects.nonNull(userData.getRefillBonusReason())) {
      userDataGrpc.setRefillBonusReason(NullableString.newBuilder()
          .setValue(userData.getRefillBonusReason()).build());
    } else {
      userDataGrpc.setRefillBonusReason(NullableString.getDefaultInstance());
    }
    if (Objects.nonNull(userData.getRefillBonusLifeTime())) {
      userDataGrpc.setRefillBonusLifeTime(NullableInt.newBuilder()
          .setValue(userData.getRefillBonusLifeTime()).build());
    } else {
      userDataGrpc.setRefillBonusLifeTime(NullableInt.getDefaultInstance());
    }
    if (Objects.nonNull(userData.getRefillBonusLifeTimeDays())) {
      userDataGrpc.setRefillBonusLifeTimeDays(NullableInt.newBuilder()
          .setValue(userData.getRefillBonusLifeTimeDays()).build());
    } else {
      userDataGrpc.setRefillBonusLifeTimeDays(NullableInt.getDefaultInstance());
    }
    return userDataGrpc.build();
  }

  public Reward toReward(RewardGrpc rg) {
    return Reward.builder()
        .id(rg.getId().hasValue() ? rg.getId().getValue() : null)
        .url(rg.getUrl().hasValue() ? rg.getUrl().getValue() : null)
        .value(rg.getValue().hasValue() ? rg.getValue().getValue() : null)
        .type(rg.getType().hasValue() ? PrizeType.valueOf(rg.getType().getValue()) : null)
        .prizeSubType(rg.getPrizeSubType().hasValue() ? PrizeSubType.valueOf(rg.getPrizeSubType().getValue()) : null)
        .description(rg.getDescription().hasValue() ? rg.getDescription().getValue() : null)
        .name(rg.getName().hasValue() ? rg.getName().getValue() : null)
        .build();
  }

  public GetAndApplyRewardsRequestGrpc toGetAndApplyRewardsRequestGrpc(List<Integer> ids, ApplyRewardUserData userData) {
    return GetAndApplyRewardsRequestGrpc.newBuilder()
        .setRandomIds(toNullableIntegerList(ids))
        .setUserData(toApplyRewardUserDataGrpc(userData))
        .build();
  }

  public static UserPuzzleWithCollectedCount toUserPuzzleWithCollectedCount(
      UserPuzzleWithCollectedCountGrpc userPuzzleWithCollectedCountGrpc
  ) {
    return UserPuzzleWithCollectedCount.builder()
        .type(userPuzzleWithCollectedCountGrpc.getType())
        .name(userPuzzleWithCollectedCountGrpc.getName())
        .url(userPuzzleWithCollectedCountGrpc.getUrl())
        .collectedCountsByRarities(userPuzzleWithCollectedCountGrpc.getCollectedCountsByRaritiesList().stream()
            .map(collectedCountsByRaritiesGrpc -> PuzzleRarityAndCollectedCount.builder()
                .rarity(collectedCountsByRaritiesGrpc.getRarity())
                .collectedCount(collectedCountsByRaritiesGrpc.getCollectedCount())
                .build())
            .collect(Collectors.toList()))
        .build();
  }

  public static PuzzleGrpc toPuzzleGrpc(Puzzle p) {
    PuzzleGrpc.Builder b = PuzzleGrpc.newBuilder();

    b.setId(p.getId());
    b.setUrl(p.getUrl());
    b.setName(p.getName());
    b.setRarity(p.getRarity());
    b.setPrize(p.getPrize());
    b.setType(p.getType());

    if (p.getGameType() != null) {
      b.setGameType(NullableString.newBuilder().setValue(p.getGameType().name()).build());
    } else {
      b.setGameType(NullableString.newBuilder().setNull(NullValue.NULL_VALUE).build());
    }

    if (p.getCollected() != null) {
      b.setCollected(NullableInt.newBuilder().setValue(p.getCollected()).build());
    } else {
      b.setCollected(NullableInt.newBuilder().setNull(NullValue.NULL_VALUE).build());
    }

    if (p.getStatus() != null) {
      b.setStatus(NullableBoolean.newBuilder().setValue(p.getStatus()).build());
    } else {
      b.setStatus(NullableBoolean.newBuilder().setNull(NullValue.NULL_VALUE).build());
    }

    return b.build();
  }

  public static Puzzle toPuzzle(PuzzleGrpc g) {
    return Puzzle.builder()
        .id(g.getId())
        .url(g.getUrl())
        .name(g.getName())
        .rarity(g.getRarity())
        .prize(g.getPrize())
        .type(g.getType())
        .gameType(g.hasGameType() && g.getGameType().hasValue() ? BonusGameType.valueOf(g.getGameType().getValue()) : null)
        .collected(g.hasCollected() && g.getCollected().hasValue() ? g.getCollected().getValue() : null)
        .status(g.hasStatus() && g.getStatus().hasValue() ? g.getStatus().getValue() : null)
        .build();
  }

  public static PuzzleRequestGrpc toPuzzleRequestGrpc(PuzzleRequest r) {
    PuzzleRequestGrpc.Builder b = PuzzleRequestGrpc.newBuilder();

    b.setName(r.getName());
    b.setType(r.getType());
    b.setRarity(r.getRarity());
    b.setUrl(r.getUrl());
    b.setPrize(r.getPrize());

    if (r.getGameType() != null) {
      b.setGameType(NullableString.newBuilder().setValue(r.getGameType()).build());
    } else {
      b.setGameType(NullableString.newBuilder().setNull(NullValue.NULL_VALUE).build());
    }

    return b.build();
  }

  public static UpdatePuzzleRequestGrpc toUpdatePuzzleRequestGrpc(Long puzzleId, PuzzleRequest r) {
    return UpdatePuzzleRequestGrpc.newBuilder()
        .setPuzzleId(puzzleId)
        .setPayload(toPuzzleRequestGrpc(r))
        .build();
  }

  public static PuzzleItemGrpc toPuzzleItemGrpc(PuzzleItem it) {
    PuzzleItemGrpc.Builder b = PuzzleItemGrpc.newBuilder();
    b.setId(it.getId());
    b.setPosition(it.getPosition());
    b.setPuzzleId(it.getPuzzleId());
    b.setUrl(it.getUrl());
    return b.build();
  }

  public static PuzzleItem toPuzzleItem(PuzzleItemGrpc g) {
    return PuzzleItem.builder()
        .id(g.getId())
        .position(g.getPosition())
        .puzzleId(g.getPuzzleId())
        .url(g.getUrl())
        .build();
  }

  public static SavePuzzleItemsRequestGrpc toSavePuzzleItemsRequestGrpc(Long puzzleId, List<PuzzleItemRequest> items) {
    SavePuzzleItemsRequestGrpc.Builder b = SavePuzzleItemsRequestGrpc.newBuilder()
        .setPuzzleId(puzzleId);

    b.addAllItems(items.stream().map(req ->
        PuzzleItemRequestGrpc.newBuilder()
            .setPuzzleItemId(req.getPuzzleItemId())
            .setUrl(req.getUrl())
            .build()
    ).collect(Collectors.toList()));

    return b.build();
  }

  public static UpdatePuzzleItemsRequestGrpc toUpdatePuzzleItemsRequestGrpc(Long puzzleId, List<PuzzleItemRequest> items) {
    UpdatePuzzleItemsRequestGrpc.Builder b = UpdatePuzzleItemsRequestGrpc.newBuilder()
        .setPuzzleId(puzzleId);

    b.addAllItems(items.stream().map(req ->
        PuzzleItemRequestGrpc.newBuilder()
            .setPuzzleItemId(req.getPuzzleItemId())
            .setUrl(req.getUrl())
            .build()
    ).collect(Collectors.toList()));

    return b.build();
  }

  public static UpdatePuzzleItemByIdRequestGrpc toUpdatePuzzleItemByIdRequestGrpc(PuzzleItem it) {
    return UpdatePuzzleItemByIdRequestGrpc.newBuilder()
        .setItem(toPuzzleItemGrpc(it))
        .build();
  }

  private static BonusGameBuyDataRequest toBonusGameBuyDataRequest(BonusGameBuyData gameData) {
    return BonusGameBuyDataRequest.newBuilder()
        .setMultiplier(gameData.getMultiplier())
        .addAllCoupons(toBonusGameBuyCouponRequestList(gameData.getCoupons()))
        .build();
  }

  private static List<BonusGameBuyCouponRequest> toBonusGameBuyCouponRequestList(List<BonusGameBuyCoupon> coupons) {
    return Optional.ofNullable(coupons)
        .map(list -> list.stream()
            .map(GrpcMapper::toBonusGameBuyCouponRequest)
            .toList()
        ).orElseGet(ArrayList::new);

  }

  private static BonusGameBuyCouponRequest toBonusGameBuyCouponRequest(BonusGameBuyCoupon coupon) {
    return BonusGameBuyCouponRequest.newBuilder()
        .addAllCombination(coupon.getCombination())
        .addAllExtraCombination(coupon.getExtraCombination())
        .setParity(toNullableString(coupon.getParity()))
        .build();
  }

  private static List<LotteryTicket> toLotteryTicketList(NullableTicketsGrpc tickets) {
    return tickets.hasTickets() ? toLotteryTicketList(tickets.getTickets()) : Collections.emptyList();
  }

  private static List<LotteryTicket> toLotteryTicketList(TicketsGrpc tickets) {
    return tickets.getTicketsList().stream().map(GrpcMapper::toLotteryTicket).toList();
  }

  private static LotteryTicket toLotteryTicket(TicketGrpc ticketGrpc) {
    LotteryTicket lotteryTicket = new LotteryTicket();
    List<RewardGrpc> rewards = ticketGrpc.getRewardsList();
    lotteryTicket.setRewards(toRewardList(rewards));
    lotteryTicket.setTotalPrize(ticketGrpc.getTotalPrize());
    TicketDataGrpc data = ticketGrpc.getData();
    LotteryTicketData lotteryTicketData = new LotteryTicketData().setMultiplier(data.getMultiplier());
    NullableTicketDataCombinationsGrpc combinations = data.getCombinations();
    if (combinations.hasCombinations()) {
      lotteryTicketData.setCombinations(toCombination(combinations.getCombinations()));
    }
    lotteryTicket.setData(lotteryTicketData);
    DrawInfoGrpc drawInfo = ticketGrpc.getDrawInfo();
    LotteryDrawInfo lotteryDrawInfo = new LotteryDrawInfo();
    lotteryDrawInfo.setParity(drawInfo.getParity());
    lotteryDrawInfo.setPlayed(drawInfo.getPlayedList());
    lotteryTicket.setDrawInfo(lotteryDrawInfo);
    return lotteryTicket;
  }

  private static LotteryTicketDataCombination toCombination(TicketDataCombinationsGrpc combinations) {
    LotteryTicketDataCombination lotteryTicketDataCombination = new LotteryTicketDataCombination();
    lotteryTicketDataCombination.setNumbers(combinations.getNumbersList());
    lotteryTicketDataCombination.setExtraNumbers(combinations.getExtraNumbersList());
    NullableString parity = combinations.getParity();
    if (parity.hasValue()) {
      lotteryTicketDataCombination.setParity(parity.getValue());
    }
    return lotteryTicketDataCombination;
  }

  private static NullableString toNullableString(String string) {
    return Objects.nonNull(string) ?
        NullableString.newBuilder().setValue(string).build() :
        NullableString.newBuilder().setNull(NullValue.NULL_VALUE).build();
  }

  public static PuzzleModel toPuzzleModel(org.orglot.gosloto.reward.grpc.Puzzle grpc) {
    PuzzleModel.PuzzleModelBuilder builder = PuzzleModel.builder()
        .url(grpc.getUrl())
        .description(grpc.getDescription());
    NullableLong id = grpc.getPuzzleId();
    if (id.hasValue()) {
      builder.id(id.getValue());
    }
    return builder.build();
  }
}
