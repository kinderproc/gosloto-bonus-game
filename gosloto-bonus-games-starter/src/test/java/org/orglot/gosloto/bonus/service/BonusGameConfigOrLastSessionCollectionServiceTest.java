package org.orglot.gosloto.bonus.service;

import org.gosloto.promo.PromoWalletGrpcClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.orglot.bonus.games.model.BonusGameType;
import org.orglot.bonus.games.model.response.GameModeResponse;
import org.orglot.bonus.games.model.response.PriceAndScaleOfMode;
import org.orglot.bonus.games.model.response.config.ModeTypeGameConfig;
import org.orglot.gosloto.bonus.client.BonusServiceClient;
import org.orglot.gosloto.bonus.games.config.RefreshBeanConfiguration;
import org.orglot.gosloto.bonus.games.model.Mode;
import org.orglot.gosloto.bonus.games.model.PuzzleItemWithSessionCreateDate;
import org.orglot.gosloto.bonus.games.model.SessionState;
import org.orglot.gosloto.bonus.games.repo.PuzzleItemRepository;
import org.orglot.gosloto.bonus.games.repo.PuzzleRepository;
import org.orglot.gosloto.bonus.games.repo.UsersPuzzleRepository;
import org.orglot.gosloto.bonus.games.repo.UsersSessionsRepository;
import org.orglot.gosloto.bonus.games.service.BonusGameSettingsService;
import org.orglot.gosloto.bonus.games.service.ModeGameConfigService;
import org.orglot.gosloto.bonus.games.service.PrizeService;
import org.orglot.gosloto.bonus.games.service.PurchaseService;
import org.orglot.gosloto.bonus.games.service.ReactiveBonusGameServiceImpl;
import org.orglot.gosloto.bonus.games.service.UserSessionService;
import org.orglot.gosloto.bonus.games.service.puzzle.collection.PuzzleCollectionService;
import org.orglot.gosloto.components.log.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(classes = {PuzzleCollectionService.class, ReactiveBonusGameServiceImpl.class,
    UserSessionService.class, PurchaseService.class, PrizeService.class, ModeGameConfigService.class, BonusGameSettingsService.class,
    RefreshBeanConfiguration.RandomConfigsBean.class, PromoWalletGrpcClient.class})
public class BonusGameConfigOrLastSessionCollectionServiceTest {
    @Autowired
    PuzzleCollectionService puzzleCollectionService;
    @Autowired
    ReactiveBonusGameServiceImpl reactiveBonusGameServiceImpl;
    @Autowired
    PurchaseService purchaseService;
    @Autowired
    PrizeService prizeService;
    @Autowired
    ModeGameConfigService modeGameConfigService;
    @MockBean
    BonusGameSettingsService bonusGameSettingsService;
    @MockBean
    PuzzleRepository puzzleRepository;
    @MockBean
    PuzzleItemRepository puzzleItemRepository;
    @MockBean
    UsersSessionsRepository usersSessionsRepository;
    @MockBean
    UsersPuzzleRepository usersPuzzleRepository;
    @MockBean
    LogRepository logRepository;
    @MockBean
    BonusServiceClient bonusServiceClient;

    @Test
    public void getUserCollectionTest() {
        Mockito.when(puzzleItemRepository.findAllEnrichUser(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(Flux.fromIterable(List.of(new PuzzleItemWithSessionCreateDate(3L, "itemUrl3", 1,
                    2L, "url2", BonusGameType.SWEETS_CARNIVAL.name(), "1", "1",
                        "1", 1, 1, false, Instant.now()),
                    new PuzzleItemWithSessionCreateDate(2L, "itemUrl1", 1,
                        2L, "url2", BonusGameType.SWEETS_CARNIVAL.name(), "1", "1",
                        "1", 1, 1, false, Instant.now()),
                    new PuzzleItemWithSessionCreateDate(1L, "itemUrl2", 2,
                        2L, "url2", BonusGameType.SWEETS_CARNIVAL.name(), "1", "1",
                        "1", 1, 1, false, Instant.now())
                       )));

        var collection = puzzleCollectionService.getUserCollection(1L, null).block();
        assertEquals(BonusGameType.SWEETS_CARNIVAL.name(), collection.get(0).getGameType());
        assertEquals(4, collection.size());
        assertEquals(2, collection.get(0).getPuzzleItems().size());
        assertEquals(1, collection.get(1).getPuzzleItems().size());
        assertNull(collection.get(2).getPuzzleItems());
        assertNull(collection.get(3).getPuzzleItems());

        assertEquals(2L, collection.get(0).getPuzzleID());
        assertEquals(3L, collection.get(1).getPuzzleID());
        assertEquals(1L, collection.get(2).getPuzzleID());
        assertEquals(4L, collection.get(3).getPuzzleID());
    }

    @Test
    public void getBonusGameStates_MINESWEEPER() {
        var userId = 1L;
        String gameType = "MINESWEEPER";
        var modes = new ArrayList<Mode>();
        modes.add(Mode.builder().number(1).price(9).description("режим1").randomPrizeIds(List.of()).randomRewardIds(List.of()).visible(true)
                .scale(List.of()).build());
        modes.add(Mode.builder().number(2).price(27).description("режим2").randomPrizeIds(List.of()).randomRewardIds(List.of())
                .visible(true).scale(List.of()).build());
        modes.add(Mode.builder().number(3).price(81).description("режим3").randomPrizeIds(List.of()).randomRewardIds(List.of())
                .visible(true).scale(List.of()).build());

        Mockito.when(usersSessionsRepository.findByUserIdAndGameTypeAndSessionStateNot(userId, gameType,
                List.of(SessionState.COMPLETED.name(), SessionState.EXPIRED.name()))).thenReturn(Mono.empty());
        Mockito.when(bonusGameSettingsService.getScale(gameType, 0)).thenReturn(Map.of());
        Mockito.when(bonusGameSettingsService.getModes(BonusGameType.valueOf(gameType))).thenReturn(modes);

        List<GameModeResponse> expectedResult = List.of(GameModeResponse.builder().modeNumber(1).description("режим1")
                        .priceAndScale(List.of(PriceAndScaleOfMode.builder().price(9).scale(Map.of()).build())).build(),
                GameModeResponse.builder().modeNumber(2).description("режим2")
                        .priceAndScale(List.of(PriceAndScaleOfMode.builder().price(27).scale(Map.of()).build())).build(),
                GameModeResponse.builder().modeNumber(3).description("режим3")
                        .priceAndScale(List.of(PriceAndScaleOfMode.builder().price(81).scale(Map.of()).build())).build());

        var gameConfig = new ModeTypeGameConfig(expectedResult, gameType);
        var result = reactiveBonusGameServiceImpl.getBonusGameConfigOrSessionUUID(gameType, userId).block();

        assertEquals(gameConfig.getModes(), result.getGameConfig().getModes());
    }

}
