package org.orglot.gosloto.bonus.games.service.grpc.mapper;

import com.google.protobuf.NullValue;
import com.google.protobuf.Timestamp;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.orglot.bonus.games.model.PrizeSubType;
import org.orglot.bonus.games.model.request.ApplyRewardUserData;
import org.orglot.bonus.games.model.request.BonusGameBuy;
import org.orglot.bonus.games.model.request.BonusGameBuyCoupon;
import org.orglot.bonus.games.model.request.BonusGameBuyData;
import org.orglot.bonus.games.model.response.BonusGameBuyStatus;
import org.orglot.bonus.games.model.response.BonusGameCollection;
import org.orglot.bonus.games.model.response.BonusGameConfigOrLastSession;
import org.orglot.bonus.games.model.response.BonusGamePlayStatus;
import org.orglot.bonus.games.model.response.BonusGameTransferStatus;
import org.orglot.bonus.games.model.response.Consumable;
import org.orglot.bonus.games.model.response.GameCompleteResult;
import org.orglot.bonus.games.model.response.GameModeResponse;
import org.orglot.bonus.games.model.response.LotteryTicket;
import org.orglot.bonus.games.model.response.LotteryTicketDataCombination;
import org.orglot.bonus.games.model.response.PriceAndScaleOfMode;
import org.orglot.bonus.games.model.response.PrizeType;
import org.orglot.bonus.games.model.response.Reward;
import org.orglot.bonus.games.model.response.config.ModeTypeGameConfig;
import org.orglot.gosloto.bonus.games.grpc.BonusGameBuyCouponRequest;
import org.orglot.gosloto.bonus.games.grpc.BonusGameBuyDataRequest;
import org.orglot.gosloto.bonus.games.grpc.BonusGameBuyRequest;
import org.orglot.gosloto.bonus.games.grpc.BonusGameBuyStatusGrpc;
import org.orglot.gosloto.bonus.games.grpc.BonusGameCollectionItem;
import org.orglot.gosloto.bonus.games.grpc.BonusGameConfigOrLastSessionResponse;
import org.orglot.gosloto.bonus.games.grpc.BonusGamePlayStatusGrpc;
import org.orglot.gosloto.bonus.games.grpc.BonusGameTransferStatusGrpc;
import org.orglot.gosloto.bonus.games.grpc.ConsumableGrpc;
import org.orglot.gosloto.bonus.games.grpc.DrawInfoGrpc;
import org.orglot.gosloto.bonus.games.grpc.GameCompleteResultGrpc;
import org.orglot.gosloto.bonus.games.grpc.GameConfig;
import org.orglot.gosloto.bonus.games.grpc.GameModeResponseGrpc;
import org.orglot.gosloto.bonus.games.grpc.GetUserCollectionElement;
import org.orglot.gosloto.bonus.games.grpc.GetUserCollectionResponse;
import org.orglot.gosloto.bonus.games.grpc.LastSession;
import org.orglot.gosloto.bonus.games.grpc.NullableBonusGameBuyDataRequest;
import org.orglot.gosloto.bonus.games.grpc.NullableLastSession;
import org.orglot.gosloto.bonus.games.grpc.NullableTicketDataCombinationsGrpc;
import org.orglot.gosloto.bonus.games.grpc.NullableTicketsGrpc;
import org.orglot.gosloto.bonus.games.grpc.PriceAndScaleOfModeGrpc;
import org.orglot.gosloto.bonus.games.grpc.StatusGrpc;
import org.orglot.gosloto.bonus.games.grpc.TicketDataCombinationsGrpc;
import org.orglot.gosloto.bonus.games.grpc.TicketDataGrpc;
import org.orglot.gosloto.bonus.games.grpc.TicketGrpc;
import org.orglot.gosloto.bonus.games.grpc.TicketsGrpc;
import org.orglot.gosloto.bonus.games.grpc.model.IntegerList;
import org.orglot.gosloto.bonus.games.grpc.model.NullableInt;
import org.orglot.gosloto.bonus.games.grpc.model.NullableInteger;
import org.orglot.gosloto.bonus.games.grpc.model.NullableIntegerList;
import org.orglot.gosloto.bonus.games.grpc.model.NullableLong;
import org.orglot.gosloto.bonus.games.grpc.model.NullableRewardsGrpc;
import org.orglot.gosloto.bonus.games.grpc.model.NullableString;
import org.orglot.gosloto.bonus.games.grpc.model.RewardGrpc;
import org.orglot.gosloto.bonus.games.grpc.model.RewardsGrpc;
import org.orglot.gosloto.bonus.games.model.Rarity;
import org.orglot.gosloto.bonus.games.model.UserPuzzleWithCollectedCount;
import org.orglot.gosloto.reward.grpc.ApplyRewardUserDataGrpc;
import org.orglot.gosloto.reward.grpc.CollectedCountsByRaritiesGrpc;
import org.orglot.gosloto.reward.grpc.RarityGrpc;
import org.orglot.gosloto.reward.grpc.UserPuzzleWithCollectedCountGrpc;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@UtilityClass
public class GrpcMapper {

    public static BonusGameConfigOrLastSessionResponse toBonusGameConfigOrUserSessionResponse(BonusGameConfigOrLastSession response) {
        if (Objects.isNull(response)) {
            return BonusGameConfigOrLastSessionResponse.getDefaultInstance();
        }
        var bonusGameConfigOrLastSessionResponse = BonusGameConfigOrLastSessionResponse.newBuilder();
        if (Objects.nonNull(response.getGameConfig())) {
            bonusGameConfigOrLastSessionResponse.setGameConfig(toGameConfig(response.getGameConfig()));
        }
        if (Objects.nonNull(response.getSessionUUID())) {
            bonusGameConfigOrLastSessionResponse
                    .setSessionUUID(NullableString.newBuilder().setValue(response.getSessionUUID().toString()).build());
        }
        if (Objects.nonNull(response.getLastSession())) {
            var lastSession = response.getLastSession();
            var consumables = lastSession.getConsumables().stream()
                    .map(GrpcMapper::toConsumableGrpc)
                    .toList();
            bonusGameConfigOrLastSessionResponse
                    .setLastSession(NullableLastSession.newBuilder()
                            .setValue(LastSession.newBuilder()
                                    .putAllScale(Objects.nonNull(lastSession.getScale()) ? lastSession.getScale() : Map.of())
                                    .setSessionUUID(lastSession.getSessionUUID().toString())
                                    .setModeNumber(Objects.nonNull(lastSession.getModeNumber()) ?
                                            NullableInt.newBuilder().setValue(lastSession.getModeNumber()).build() :
                                            NullableInt.newBuilder().setNull(NullValue.NULL_VALUE).build())
                                    .setPrice(Objects.nonNull(lastSession.getPrice()) ?
                                            NullableInt.newBuilder().setValue(lastSession.getPrice()).build() :
                                            NullableInt.newBuilder().setNull(NullValue.NULL_VALUE).build())
                                    .setPrizes(Objects.nonNull(lastSession.getPrizes()) ?
                                            NullableIntegerList.newBuilder().setData(toIntegerList(lastSession.getPrizes())).build() :
                                            NullableIntegerList.newBuilder().setNull(NullValue.NULL_VALUE).build())
                                    .addAllConsumables(consumables)
                                    .build())
                            .build());
        }
        return bonusGameConfigOrLastSessionResponse.build();
    }

    public static IntegerList toIntegerList(List<Integer> integerList) {
        return IntegerList.newBuilder().addAllData(integerList.stream()
                .map(integer -> Objects.isNull(integer) ? NullableInteger.newBuilder().setNull(NullValue.NULL_VALUE).build() :
                        NullableInteger.newBuilder().setValue(integer).build())
                .collect(Collectors.toList())
        ).build();
    }

    public static List<Integer> toIntegerList(NullableIntegerList integerList) {
        if (!integerList.hasData()) {
            return Collections.emptyList();
        }
        return integerList.getData().getDataList().stream()
            .map(nullableInteger -> nullableInteger.hasValue() ? nullableInteger.getValue() : null)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    public static org.orglot.gosloto.bonus.games.grpc.NullableGameConfig toGameConfig(ModeTypeGameConfig gameConfig) {
        return org.orglot.gosloto.bonus.games.grpc.NullableGameConfig.newBuilder()
                .setValue(GameConfig.newBuilder()
                        .setGameType(gameConfig.getGameType())
                        .addAllModes(toGameModeResponseGrpc(gameConfig.getModes()))
                        .build())
                .build();
    }

    public static List<GameModeResponseGrpc> toGameModeResponseGrpc(List<GameModeResponse> response) {
        return response.stream()
                .map(mode -> GameModeResponseGrpc.newBuilder()
                        .addAllPriceAndScale(toPriceAndScaleOfModeGrpcList(mode.getPriceAndScale()))
                        .setModeNumber(mode.getModeNumber())
                        .setDescription(StringUtils.isBlank(mode.getDescription()) ?
                                NullableString.getDefaultInstance() :
                                NullableString.newBuilder().setValue(mode.getDescription()).build())
                        .build())
                .collect(Collectors.toList());
    }

    private List<PriceAndScaleOfModeGrpc> toPriceAndScaleOfModeGrpcList(List<PriceAndScaleOfMode> priceAndScaleOfModes) {
        return priceAndScaleOfModes.stream()
                .map(priceAndScaleOfMode -> PriceAndScaleOfModeGrpc.newBuilder()
                        .setPrice(priceAndScaleOfMode.getPrice())
                        .putAllScale(priceAndScaleOfMode.getScale())
                        .build())
                .collect(Collectors.toList());
    }

    public static BonusGameBuy toBonusGameBuy(BonusGameBuyRequest request) {
        NullableBonusGameBuyDataRequest gameData = request.getGameData();
        return BonusGameBuy.builder()
                .gameType(request.getGameType())
                .mode(request.getMode().hasValue() ? request.getMode().getValue() : null)
                .price(request.getPrice().hasValue() ? request.getPrice().getValue() : null)
                .userId(request.getUserId())
                .mobile(request.getMobile())
                .devicePlatform(request.getDevicePlatform())
                .gameData(gameData.hasValue() ? toBonusGameBuyData(gameData.getValue()) : null)
                .consumableId(request.getConsumableId().hasValue() ? request.getConsumableId().getValue() : null)
                .build();
    }

    private static BonusGameBuyData toBonusGameBuyData(BonusGameBuyDataRequest gameBuyDataRequest) {
        return BonusGameBuyData.builder()
            .multiplier(gameBuyDataRequest.getMultiplier())
            .coupons(toBonusGameBuyCouponList(gameBuyDataRequest.getCouponsList()))
            .build();
    }

    private static List<BonusGameBuyCoupon> toBonusGameBuyCouponList(List<BonusGameBuyCouponRequest> coupons) {
        return Optional.ofNullable(coupons)
            .map(list -> list.stream()
                .map(GrpcMapper::toBonusGameBuyCoupon)
                .toList()
            ).orElseGet(ArrayList::new);

    }

    private static BonusGameBuyCoupon toBonusGameBuyCoupon(BonusGameBuyCouponRequest coupon) {
        return BonusGameBuyCoupon.builder()
            .combination(coupon.getCombinationList())
            .extraCombination(coupon.getExtraCombinationList())
            .parity(coupon.getParity().hasValue() ? coupon.getParity().getValue() : null)
            .build();
    }

    public static BonusGameBuyStatusGrpc toBonusGameBuyStatusGrpc(BonusGameBuyStatus bonusGameBuyStatus) {
        var bonusGameBuyStatusGrpc = BonusGameBuyStatusGrpc.newBuilder()
                .setStatus(StatusGrpc.valueOf(bonusGameBuyStatus.getStatus().name()));

        if (Objects.nonNull(bonusGameBuyStatus.getSessionUUID())) {
            bonusGameBuyStatusGrpc.setSessionUUID(NullableString.newBuilder()
                    .setValue(bonusGameBuyStatus.getSessionUUID().toString())
                    .build());
        }

        if (Objects.nonNull(bonusGameBuyStatus.getError())) {
            bonusGameBuyStatusGrpc.setError(NullableString.newBuilder()
                    .setValue(bonusGameBuyStatus.getError())
                    .build());
        }
        return bonusGameBuyStatusGrpc.build();
    }

    public static BonusGamePlayStatusGrpc toBonusGamePlayStatusGrpc(BonusGamePlayStatus bonusGamePlayStatus) {
        var bonusGameBuyStatusGrpc = BonusGamePlayStatusGrpc.newBuilder()
                .setStatus(StatusGrpc.valueOf(bonusGamePlayStatus.getStatus().name()));

        if (Objects.nonNull(bonusGamePlayStatus.getSessionUUID())) {
            bonusGameBuyStatusGrpc.setSessionUUID(NullableString.newBuilder()
                    .setValue(bonusGamePlayStatus.getSessionUUID().toString())
                    .build());
        }

        if (Objects.nonNull(bonusGamePlayStatus.getError())) {
            bonusGameBuyStatusGrpc.setError(NullableString.newBuilder()
                    .setValue(bonusGamePlayStatus.getError())
                    .build());
        }

        if (Objects.nonNull(bonusGamePlayStatus.getPrizes())) {
            bonusGameBuyStatusGrpc.setPrizes(NullableIntegerList.newBuilder()
                    .setData(toIntegerList(bonusGamePlayStatus.getPrizes()))
                    .build()
            );
        } else {
            bonusGameBuyStatusGrpc.setPrizes(NullableIntegerList.newBuilder()
                    .setNull(NullValue.NULL_VALUE)
                    .build()
            );
        }

        if (Objects.nonNull(bonusGamePlayStatus.getScale())) {
            bonusGameBuyStatusGrpc.putAllScale(bonusGamePlayStatus.getScale());
        }

        if (Objects.nonNull(bonusGamePlayStatus.getConsumables())) {
            bonusGameBuyStatusGrpc.addAllConsumables(bonusGamePlayStatus.getConsumables().stream()
                    .map(GrpcMapper::toConsumableGrpc)
                    .toList());
        }

        return bonusGameBuyStatusGrpc.build();
    }

    private static ConsumableGrpc toConsumableGrpc(Consumable c) {
        return ConsumableGrpc.newBuilder()
                .setId(Objects.nonNull(c.getId()) ?
                        NullableLong.newBuilder().setValue(c.getId()).build() :
                        NullableLong.newBuilder().setNull(NullValue.NULL_VALUE).build())
                .setName(
                        StringUtils.isBlank(c.getName()) ?
                                NullableString.getDefaultInstance() :
                                NullableString.newBuilder().setValue(c.getName()).build())
                .setPrice(Objects.nonNull(c.getPrice()) ?
                        NullableLong.newBuilder().setValue(c.getPrice()).build() :
                        NullableLong.newBuilder().setNull(NullValue.NULL_VALUE).build())
                .setAvailable(true)
                .setWeight(Objects.nonNull(c.getWeight()) ?
                        NullableInt.newBuilder().setValue(c.getWeight()).build() :
                        NullableInt.newBuilder().setNull(NullValue.NULL_VALUE).build())
                .setIconUrl(Objects.nonNull(StringUtils.isBlank(c.getIconUrl())) ?
                        NullableString.getDefaultInstance() :
                        NullableString.newBuilder().setValue(c.getIconUrl()).build())
                .build();
    }

    public static GameCompleteResultGrpc toGameCompleteResultGrpc(GameCompleteResult gameCompleteResult) {
        if (Objects.isNull(gameCompleteResult.getTotalPrize())) {
            log.warn("Total prize is null for gameCompleteResult {}", gameCompleteResult);
        }
        var gameCompleteResultGrpc = GameCompleteResultGrpc.newBuilder()
                .setStatus(StatusGrpc.valueOf(gameCompleteResult.getStatus().name()));
            gameCompleteResultGrpc.setTotalPrize(Objects.isNull(gameCompleteResult.getTotalPrize()) ? 0 :
                    gameCompleteResult.getTotalPrize());
        if (Objects.nonNull(gameCompleteResult.getError())) {
            gameCompleteResultGrpc.setError(NullableString.newBuilder().setValue(gameCompleteResult.getError()).build());
        } else {
            gameCompleteResultGrpc.setError(NullableString.newBuilder().setNull(NullValue.NULL_VALUE).build());

        }
        gameCompleteResultGrpc.setRewards(toNullableRewardsGrpc(gameCompleteResult.getRewards()));
        if (CollectionUtils.isEmpty(gameCompleteResult.getRewards())) {
            gameCompleteResultGrpc.setRewards(NullableRewardsGrpc.newBuilder()
                    .setNull(NullValue.NULL_VALUE)
                    .build()
            );
        } else {
            gameCompleteResultGrpc.setRewards(NullableRewardsGrpc.newBuilder()
                    .setRewards(RewardsGrpc.newBuilder()
                            .addAllReward(toRewardGrpcList(gameCompleteResult.getRewards()))
                            .build())
                    .build());
        }
        gameCompleteResultGrpc.setTickets(toNullableTicketsGrpc(gameCompleteResult.getTickets()));
        return gameCompleteResultGrpc.build();
    }

    private NullableRewardsGrpc toNullableRewardsGrpc(List<Reward> rewards) {
        if (CollectionUtils.isEmpty(rewards)) {
            return NullableRewardsGrpc.newBuilder()
                .setNull(NullValue.NULL_VALUE)
                .build();
        }
        return NullableRewardsGrpc.newBuilder()
            .setRewards(
                RewardsGrpc.newBuilder()
                  .addAllReward(toRewardGrpcList(rewards))
                  .build()
            )
            .build();
    }

    private NullableTicketsGrpc toNullableTicketsGrpc(List<LotteryTicket> tickets) {
        if (!CollectionUtils.isEmpty(tickets)) {
            return NullableTicketsGrpc
                .newBuilder()
                .setTickets(
                    TicketsGrpc.newBuilder().addAllTickets(toTicketGrpcList(tickets)).build()
                )
                .build();
        }
        return NullableTicketsGrpc.newBuilder().setNull(NullValue.NULL_VALUE).build();
    }

    private List<TicketGrpc> toTicketGrpcList(List<LotteryTicket> tickets) {
        return tickets.stream().map(GrpcMapper::toTicketGrpc).toList();
    }

    private TicketGrpc toTicketGrpc(LotteryTicket ticket) {
        var drawInfo = Optional.ofNullable(ticket.getDrawInfo())
            .orElseThrow(() -> new RuntimeException("Field drawInfo mas be not null for Lottery"));
        var data = Optional.ofNullable(ticket.getData())
            .orElseThrow(() -> new RuntimeException("Field data mas be not null for Lottery"));
        var drawBuilder = DrawInfoGrpc.newBuilder()
            .addAllPlayed(drawInfo.getPlayed());
        if (drawInfo.getParity() != null) {
            drawBuilder.setParity(drawInfo.getParity());
        }
        return TicketGrpc
            .newBuilder()
            .setTotalPrize(ticket.getTotalPrize())
            .addAllRewards(toRewardGrpcList(ticket.getRewards()))
            .setData(
                TicketDataGrpc.newBuilder()
                    .setMultiplier(data.getMultiplier())
                    .setCombinations(
                        Optional.ofNullable(data.getCombinations())
                            .map(combinations ->
                                NullableTicketDataCombinationsGrpc.newBuilder()
                                    .setCombinations(toTicketDataCombinationsGrpc(combinations))
                                    .build()
                            )
                            .orElseGet(() -> NullableTicketDataCombinationsGrpc.newBuilder()
                                .setNull(NullValue.NULL_VALUE)
                                .build()
                            )
                    )
                    .build()
            )
            .setDrawInfo(drawBuilder.build())
            .build();
    }

    private TicketDataCombinationsGrpc toTicketDataCombinationsGrpc(LotteryTicketDataCombination combination) {
        return TicketDataCombinationsGrpc.newBuilder()
            .addAllNumbers(combination.getNumbers())
            .addAllExtraNumbers(combination.getExtraNumbers())
            .setParity(
                Optional.ofNullable(combination.getParity())
                    .map(value -> NullableString.newBuilder().setValue(value).build())
                    .orElseGet(() -> NullableString.newBuilder().setNull(NullValue.NULL_VALUE).build()))
            .build();
    }

    public List<RewardGrpc> toRewardGrpcList(List<Reward> rewards) {
        return rewards.stream()
                .filter(reward -> !PrizeType.EMPTY.equals(reward.getType()))
                .map(GrpcMapper::toRewardGrpc)
                .collect(Collectors.toList());
    }

    public static GetUserCollectionResponse toGetUserCollectionResponse(List<BonusGameCollection> collection) {
        List<GetUserCollectionElement> list = collection.stream()
                .map(element -> GetUserCollectionElement.newBuilder()
                        .setGameType(Objects.nonNull(element.getGameType()) ? element.getGameType() : "")
                        .setPuzzleID(element.getPuzzleID())
                        .setCreateDate(dateToTimestamp(element.getCreateDate()))
                        .setPuzzleURL(element.getPuzzleURL())
                        .setRarity(element.getRarity())
                        .setPrize(element.getPrize())
                        .setName(element.getName())
                        .setType(element.getType())
                        .setCollected(element.getCollected())
                        .setStatus(element.getStatus())
                        .addAllPuzzleItems(toPuzzleItems(element.getPuzzleItems()))
                        .build())
                .collect(Collectors.toList());
        return GetUserCollectionResponse.newBuilder().addAllGetUserCollectionElements(list).build();
    }

    public static Timestamp dateToTimestamp(Instant date) {
        if (Objects.isNull(date)) {
            return Timestamp.getDefaultInstance();
        }
        return Timestamp.newBuilder().setSeconds(date.getEpochSecond()).setNanos(date.getNano()).build();
    }

    public static List<BonusGameCollectionItem> toPuzzleItems(List<org.orglot.bonus.games.model.response.BonusGameCollectionItem> items) {
        if (Objects.isNull(items)) {
            return Collections.emptyList();
        }
        return items.stream()
                .map(item -> BonusGameCollectionItem.newBuilder().setId(item.getId()).setUrl(item.getUrl())
                        .setPuzzleItemId(item.getPuzzleItemId()).build())
                .collect(Collectors.toList());
    }

    public static BonusGameTransferStatusGrpc toBonusGameTransferStatusGrpc(BonusGameTransferStatus bonusGameTransferStatus) {
        var bonusGameTransferStatusGrpc = BonusGameTransferStatusGrpc.newBuilder()
                .setStatus(StatusGrpc.valueOf(bonusGameTransferStatus.getStatus().name()));
        if (Objects.nonNull(bonusGameTransferStatus.getError())) {
            bonusGameTransferStatusGrpc.setError(NullableString.newBuilder()
                    .setValue(bonusGameTransferStatus.getError())
                    .build());
        }
        return bonusGameTransferStatusGrpc.build();
    }

    public static RewardsGrpc toRewardsGrpc(List<Reward> rewards) {
        return RewardsGrpc.newBuilder().addAllReward(toRewardGrpcList(rewards)).build();
    }

    public static RewardGrpc toRewardGrpc(Reward reward) {
        var r = RewardGrpc.newBuilder();
        if (Objects.nonNull(reward.getId())) {
            r.setId(NullableLong.newBuilder().setValue(reward.getId()).build());
        } else {
            r.setId(NullableLong.getDefaultInstance());
        }
        if (Objects.nonNull(reward.getUrl())) {
            r.setUrl(NullableString.newBuilder().setValue(reward.getUrl()).build());
        } else {
            r.setUrl(NullableString.getDefaultInstance());
        }
        if (Objects.nonNull(reward.getValue())) {
            r.setValue(NullableInt.newBuilder().setValue(reward.getValue()).build());
        } else {
            r.setValue(NullableInt.getDefaultInstance());
        }
        if (Objects.nonNull(reward.getPrizeSubType())) {
            r.setPrizeSubType(NullableString.newBuilder().setValue(reward.getPrizeSubType().name()).build());
        } else {
            r.setPrizeSubType(NullableString.getDefaultInstance());
        }
        if (Objects.nonNull(reward.getType())) {
            r.setType(NullableString.newBuilder().setValue(reward.getType().name()).build());
        } else {
            r.setType(NullableString.getDefaultInstance());
        }
        if (Objects.nonNull(reward.getDescription())) {
            r.setDescription(NullableString.newBuilder().setValue(reward.getDescription()).build());
        } else {
            r.setDescription(NullableString.getDefaultInstance());
        }
        if (Objects.nonNull(reward.getName())) {
            r.setName(NullableString.newBuilder().setValue(reward.getName()).build());
        } else {
            r.setName(NullableString.getDefaultInstance());
        }
        return r.build();
    }

    public List<Reward> fromRewardsGrpc(RewardsGrpc rewardsGrpc) {
        return rewardsGrpc.getRewardList().stream()
            .map(rewardGrpc -> Reward.builder()
                    .type(rewardGrpc.getType().hasValue() ? PrizeType.valueOf(rewardGrpc.getType().getValue()) : null)
                    .prizeSubType(rewardGrpc.getPrizeSubType().hasValue() ? PrizeSubType.valueOf(rewardGrpc.getPrizeSubType().getValue()) :
                        null)
                    .value(rewardGrpc.getValue().hasValue() ? rewardGrpc.getValue().getValue() : null)
                    .id(rewardGrpc.getId().hasValue() ? rewardGrpc.getId().getValue() : null)
                    .url(rewardGrpc.getUrl().hasValue() ? rewardGrpc.getUrl().getValue() : null)
                    .build()
            ).toList();
    }

    public ApplyRewardUserData toApplyRewardUserData(ApplyRewardUserDataGrpc userDataGrpc) {
        return ApplyRewardUserData.builder()
            .userId(userDataGrpc.getUserId())
            .mobile(userDataGrpc.getMobile().hasValue() ? userDataGrpc.getMobile().getValue() : null)
            .bonusGameSessionUUID(userDataGrpc.getBonusGameSessionUUID().hasValue() ?
                UUID.fromString(userDataGrpc.getBonusGameSessionUUID().getValue()) : null)
            .refillBonusReason(userDataGrpc.getRefillBonusReason().hasValue() ?
                userDataGrpc.getRefillBonusReason().getValue() : null)
            .refillBonusDescription(userDataGrpc.getRefillBonusDescription().hasValue() ?
                userDataGrpc.getRefillBonusDescription().getValue() : null)
            .refillBonusLifeTime(userDataGrpc.getRefillBonusLifeTime().hasValue() ?
                userDataGrpc.getRefillBonusLifeTime().getValue() : null)
            .refillBonusLifeTimeDays(userDataGrpc.getRefillBonusLifeTimeDays().hasValue() ?
                userDataGrpc.getRefillBonusLifeTimeDays().getValue() : null)
            .build();
    }

    public static RarityGrpc toRarityGrpc(Rarity rarity) {
        return RarityGrpc.newBuilder()
            .setName(rarity.getType())
            .setTitle(rarity.getTitle())
            .setOrder(rarity.getId())
            .build();
    }

    public static UserPuzzleWithCollectedCountGrpc toUserPuzzleWithCollectedCountGrpc(UserPuzzleWithCollectedCount rarity) {
        return UserPuzzleWithCollectedCountGrpc.newBuilder()
            .setType(rarity.getType())
            .setName(rarity.getName())
            .setUrl(rarity.getUrl())
            .addAllCollectedCountsByRarities(rarity.getCollectedCountsByRarities().stream()
                .map(ccr -> CollectedCountsByRaritiesGrpc.newBuilder()
                    .setRarity(ccr.getRarity())
                    .setCollectedCount(ccr.getCollectedCount())
                    .build()
                )
                .collect(Collectors.toList())
            )
            .build();
    }
}
