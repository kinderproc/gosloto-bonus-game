package org.orglot.gosloto.bonus.games.service.puzzle.collection;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.orglot.gosloto.bonus.games.model.Rarity;
import org.orglot.gosloto.bonus.games.repo.RarityRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RarityService {

  private final RarityRepository rarityRepository;

  public Flux<Rarity> findAllByDisplay(Boolean display) {
    return rarityRepository.findAllByDisplay(display)
        .onErrorResume(e ->
            Mono.error(new RuntimeException(String.format("Error when find rarities %s %s", display, e.getMessage()))));
  }

}
