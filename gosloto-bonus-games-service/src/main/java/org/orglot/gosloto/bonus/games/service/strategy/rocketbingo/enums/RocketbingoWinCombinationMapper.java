package org.orglot.gosloto.bonus.games.service.strategy.rocketbingo.enums;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.orglot.gosloto.bonus.games.service.strategy.rocketbingo.model.RocketbingoCalculateData;
import org.orglot.gosloto.bonus.games.service.strategy.rocketbingo.model.RocketbingoCombinationData;

@Mapper
public interface RocketbingoWinCombinationMapper {

  RocketbingoWinCombinationMapper MAPPER = Mappers.getMapper(RocketbingoWinCombinationMapper.class);

  @Mapping(target = "winningNumbers", source = "structured.played")
  @Mapping(target = "numbers", source = "numbers")
  RocketbingoCalculateData toRocketbingoWinCombinationCalculateData(RocketbingoCombinationData data);
}
