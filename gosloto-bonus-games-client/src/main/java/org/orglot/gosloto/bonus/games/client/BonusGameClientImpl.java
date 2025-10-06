package org.orglot.gosloto.bonus.games.client;

import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;
import org.orglot.gosloto.bonus.games.client.operations.main.BonusGameOperations;
import org.orglot.gosloto.bonus.games.client.operations.main.BonusGameOperationsImpl;
import org.orglot.gosloto.bonus.games.client.operations.main.PuzzleCollectionOperations;
import org.orglot.gosloto.bonus.games.client.operations.main.PuzzleCollectionOperationsImpl;
import org.orglot.gosloto.bonus.games.client.operations.main.PuzzleItemsOperations;
import org.orglot.gosloto.bonus.games.client.operations.main.PuzzleItemsOperationsImpl;
import org.orglot.gosloto.bonus.games.client.operations.main.PuzzleOperations;
import org.orglot.gosloto.bonus.games.client.operations.main.PuzzleOperationsImpl;
import org.orglot.gosloto.bonus.games.client.operations.main.RewardOperations;
import org.orglot.gosloto.bonus.games.client.operations.main.RewardOperationsImpl;
import org.orglot.gosloto.grpc.starter.client.GrpcClient;

import java.util.Objects;

@Slf4j
public class BonusGameClientImpl implements BonusGameClient {

    private BonusGameOperations bonusGameOperations;
    private RewardOperations rewardOperations;
    private PuzzleCollectionOperations puzzleCollectionOperations;
    private PuzzleItemsOperations puzzleItemsOperations;
    private PuzzleOperations puzzleOperations;
    private GrpcClient grpcClient;

    public BonusGameClientImpl(GrpcClient grpcClient) {
        try {
            this.grpcClient = grpcClient;
            ManagedChannel grpcChannel = grpcClient.getChannel();
            this.bonusGameOperations = new BonusGameOperationsImpl(grpcChannel);
            this.rewardOperations = new RewardOperationsImpl(grpcChannel);
            this.puzzleCollectionOperations = new PuzzleCollectionOperationsImpl(grpcChannel);
            this.puzzleItemsOperations = new PuzzleItemsOperationsImpl(grpcChannel);
            this.puzzleOperations = new PuzzleOperationsImpl(grpcChannel);
        } catch (Exception exception) {
            log.error("Error while eager bonus game grpc connection ", exception);
        }
    }

    @Override
    public BonusGameOperations bonusGameOperations() {
        if (Objects.isNull(this.bonusGameOperations)) {
            this.bonusGameOperations = new BonusGameOperationsImpl(grpcClient.getChannel());
        }
        return this.bonusGameOperations;
    }

    @Override
    public RewardOperations rewardOperations() {
        if (Objects.isNull(this.rewardOperations)) {
            this.rewardOperations = new RewardOperationsImpl(grpcClient.getChannel());
        }
        return this.rewardOperations;
    }

    @Override
    public PuzzleCollectionOperations puzzleCollectionOperations() {
        if (Objects.isNull(this.puzzleCollectionOperations)) {
            this.puzzleCollectionOperations = new PuzzleCollectionOperationsImpl(grpcClient.getChannel());
        }
        return this.puzzleCollectionOperations;
    }

    @Override
    public PuzzleItemsOperations puzzleItemsOperations() {
        if (Objects.isNull(this.puzzleItemsOperations)) {
            this.puzzleItemsOperations = new PuzzleItemsOperationsImpl(grpcClient.getChannel());
        }
        return this.puzzleItemsOperations;
    }

    @Override
    public PuzzleOperations puzzleOperations() {
        if (Objects.isNull(this.puzzleOperations)) {
            this.puzzleOperations = new PuzzleOperationsImpl(grpcClient.getChannel());
        }
        return this.puzzleOperations;
    }

}
