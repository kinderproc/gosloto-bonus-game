package org.orglot.gosloto.bonus.games.config;

import lombok.RequiredArgsConstructor;
import org.orglot.bonus.games.model.BonusGameType;
import org.orglot.gosloto.bonus.games.model.BonusGameConfig;
import org.orglot.gosloto.starter.config.PropertyDescriptor;
import org.orglot.gosloto.starter.config.RefreshBean;
import org.orglot.gosloto.starter.config.UpdatableObject;

import java.util.Locale;
import java.util.Set;

@RefreshBean
@RequiredArgsConstructor
public class BonusGameConfigRefreshBean implements UpdatableObject {

  private final BonusGameType game;
  private BonusGameConfig config;

  @Override
  public Set<PropertyDescriptor> propertiesList() {
    return Set.of(new PropertyDescriptor(game.name().toLowerCase(Locale.ROOT), BonusGameConfig.class, true, "config"));
  }

  public BonusGameConfig getConfig() {
    return config;
  }

}
