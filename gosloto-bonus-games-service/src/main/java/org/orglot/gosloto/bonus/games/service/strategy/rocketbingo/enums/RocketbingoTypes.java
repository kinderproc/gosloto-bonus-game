package org.orglot.gosloto.bonus.games.service.strategy.rocketbingo.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Getter
public enum RocketbingoTypes {
  DIAGONAL_T3(getDiagonal(), 28),
  DIAGONAL_T4(getDiagonal(), 35),
  STRAIGHT_T1(getStraight(), 6),
  STRAIGHT_T2(getStraight(), 15),
  STRAIGHT_T3(getStraight(), 28),
  STRAIGHT_T4(getStraight(), 35),
  MEDIUM_T2(getMedium(), 15),
  MEDIUM_T3(getMedium(), 28),
  MEDIUM_T4(getMedium(), 35),
  FRAME_T2(getFrame(), 16),
  FRAME_T3(getFrame(), 28),
  FRAME_T4(getFrame(), 35),
  FRAME_M28(getFrame(), 28),
  WITHOUT(Collections.emptyList(), 0);

  private final List<List<Integer>> winningNumbers;
  private final int maxOrderNumber;

  private static List<List<Integer>> getDiagonal() {
    return List.of(List.of(1, 7, 18, 24), List.of(5, 9, 16, 20));
  }

  private static List<List<Integer>> getStraight() {
    return List.of(
        List.of(1, 2, 3, 4, 5),
        List.of(6, 7, 8, 9, 10),
        List.of(15, 16, 17, 18, 19),
        List.of(20, 21, 22, 23, 24),
        List.of(1, 6, 11, 15, 20),
        List.of(2, 7, 12, 16, 21),
        List.of(4, 9, 13, 18, 23),
        List.of(5, 10, 14, 19, 24)
    );
  }

  private static List<List<Integer>> getMedium() {
    return List.of(List.of(11, 12, 13, 14), List.of(3, 8, 17, 22));
  }

  private static List<List<Integer>> getFrame() {
    return List.of(List.of(1, 2, 3, 4, 5, 6, 10, 11, 14, 15, 19, 20, 21, 22, 23, 24));
  }
}
