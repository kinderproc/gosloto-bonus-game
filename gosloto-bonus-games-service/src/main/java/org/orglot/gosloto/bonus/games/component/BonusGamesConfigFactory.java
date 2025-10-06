package org.orglot.gosloto.bonus.games.component;

import org.orglot.bonus.games.model.BonusGameType;
import org.orglot.gosloto.bonus.games.config.BonusGameConfigRefreshBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class BonusGamesConfigFactory implements BeanFactoryPostProcessor {

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
    for (BonusGameType value : BonusGameType.values()) {
      var defBuilder = BeanDefinitionBuilder.rootBeanDefinition(BonusGameConfigRefreshBean.class);
      var def = defBuilder.addConstructorArgValue(value)
        .getBeanDefinition();
      var beanName = value.name().toLowerCase(Locale.ROOT) + "GameConfigRefreshBean";
      def.addQualifier(new AutowireCandidateQualifier(Qualifier.class, value.name().toLowerCase(Locale.ROOT)));
      ((BeanDefinitionRegistry) configurableListableBeanFactory).registerBeanDefinition(beanName, def);
    }
  }
}
