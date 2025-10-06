package org.orglot.gosloto.bonus.games.config;

import lombok.Getter;
import org.orglot.gosloto.bonus.games.model.prize.BonusGameRandoms;
import org.orglot.gosloto.starter.config.RefreshBean;
import org.orglot.gosloto.starter.config.RefreshBeanProperty;
import org.orglot.gosloto.starter.config.RefreshObject;
import org.orglot.gosloto.starter.config.dictionaries.DictionaryConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(DictionaryConfiguration.class)
public class RefreshBeanConfiguration {

  @Bean
  public RandomConfigsBean cmsLotteriesBean() {
    return new RandomConfigsBean();
  }

  @Getter
  @RefreshBean
  public static class RandomConfigsBean implements RefreshObject {
    @RefreshBeanProperty(name = "bonus_game_randoms")
    private BonusGameRandoms randoms;
  }

}
