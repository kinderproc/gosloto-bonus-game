package org.orglot.gosloto.bonus.games.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.orglot.gosloto.bonus.games.exception.IllegalProfileElementException;
import org.orglot.gosloto.user.service.client.grpc.UserServiceGrpcClient;
import org.orglot.gosloto.user.service.model.elements.ProfileElement;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Сервис для работы с профилем пользователя
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserProfileService {

  private final UserServiceGrpcClient userServiceGrpcClient;

  /**
   * Добавление элемента в профиль пользователя
   *
   * @param userId      ID пользователя
   * @param elementType Тип элемента
   * @param elementId   ID элемента
   */
  public Mono<Void> addUserElement(long userId, @NonNull String elementType, int elementId) {
    return userServiceGrpcClient.profileElementOperation().getAllByUser(userId, elementType)
        .flatMap(elements -> {
          // Ищем элемент
          var element = elements.stream()
              .filter(el -> el.getId() == elementId)
              .findFirst()
              .orElse(null);
          // Если элемент не нашли, ничего не делаем
          if (element == null || !element.getType().equals(elementType)) {
            return Mono.error(new IllegalProfileElementException("Element %s with id %d not found"
                .formatted(elementType, elementId)));
          }
          // Если нашли и он уже есть у пользователя, ничего не делаем
          if (element.isAcquired()) {
            return Mono.error(new IllegalProfileElementException("User already have element %s with id %d"
                .formatted(elementType, elementId)));
          }
          // Добавляем элемент пользователю
          return userServiceGrpcClient.profileElementOperation().addUserElement(userId, elementType, elementId)
              .doOnSuccess(res -> log.debug("Element {} with id {} was added to profile of user {}", elementType, elementId, userId));
        });
  }

  public Mono<String> getElementUrl(int id) {
    return userServiceGrpcClient.profileElementOperation().getById(id).map(ProfileElement::getLink);
  }

}
