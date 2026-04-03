package kr.co.jparangdev.boardbuddy.persistence.seeding;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.co.jparangdev.boardbuddy.application.game.usecase.GameCommandUseCase;
import kr.co.jparangdev.boardbuddy.application.game.usecase.GameQueryUseCase;
import kr.co.jparangdev.boardbuddy.application.seeding.port.GameDataSeeder;
import kr.co.jparangdev.boardbuddy.domain.game.Game;
import kr.co.jparangdev.boardbuddy.domain.game.ScoreStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Infrastructure adapter for seeding official game data.
 * Implements the GameDataSeeder port defined in application layer.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JpaGameDataSeeder implements GameDataSeeder {

    private final GameQueryUseCase gameQueryUseCase;
    private final GameCommandUseCase gameCommandUseCase;

    @Override
    @Transactional
    public void seed() {
        var existingGames = gameQueryUseCase.getGameList();

        List<SeedGame> seedGames = List.of(
            new SeedGame("Catan", "카탄", "Catan", 3, 4, ScoreStrategy.RANK_SCORE),
            new SeedGame("Splendor", "스플렌더", "Splendor", 2, 4, ScoreStrategy.RANK_SCORE),
            new SeedGame("Ticket to Ride", "티켓 투 라이드", "Ticket to Ride", 2, 5, ScoreStrategy.RANK_SCORE),
            new SeedGame("Azul", "아줄", "Azul", 2, 4, ScoreStrategy.RANK_SCORE),
            new SeedGame("7 Wonders", "7 원더스", "7 Wonders", 2, 7, ScoreStrategy.RANK_SCORE),
            new SeedGame("Dominion", "도미니언", "Dominion", 2, 4, ScoreStrategy.RANK_SCORE),
            new SeedGame("Codenames", "코드네임", "Codenames", 4, 8, ScoreStrategy.WIN_LOSE),
            new SeedGame("Pandemic", "팬데믹", "Pandemic", 2, 4, ScoreStrategy.COOPERATIVE),
            new SeedGame("The Resistance", "레지스탕스", "The Resistance", 5, 10, ScoreStrategy.WIN_LOSE),
            new SeedGame("Uno", "우노", "Uno", 2, 10, ScoreStrategy.RANK_SCORE),
            new SeedGame("Love Letter", "러브레터", "Love Letter", 2, 6, ScoreStrategy.RANK_SCORE),
            new SeedGame("Hanabi", "하나비", "Hanabi", 2, 5, ScoreStrategy.COOPERATIVE),
            new SeedGame("Dixit", "딕싯", "Dixit", 3, 8, ScoreStrategy.RANK_SCORE),
            new SeedGame("윷놀이", "윷놀이", "Yut Nori", 2, 4, ScoreStrategy.RANK_SCORE),
            new SeedGame("할리갈리", "할리갈리", "Halli Galli", 2, 6, ScoreStrategy.RANK_ONLY),
            new SeedGame("뱅!", "뱅!", "Bang!", 4, 7, ScoreStrategy.WIN_LOSE),
            new SeedGame("루미큐브", "루미큐브", "Rummikub", 2, 4, ScoreStrategy.RANK_SCORE)
        );

        int inserted = 0;
        int updated = 0;

        for (SeedGame seed : seedGames) {
            var existingGameOpt = existingGames.stream()
                    .filter(g -> g.getName().equals(seed.name()))
                    .findFirst();

            if (existingGameOpt.isPresent()) {
                Game existing = existingGameOpt.get();
                boolean needsUpdate = !seed.nameKo().equals(existing.getNameKo()) ||
                                      !seed.nameEn().equals(existing.getNameEn());

                if (needsUpdate) {
                    gameCommandUseCase.updateGame(existing.getId(), seed.nameKo(), seed.nameEn());
                    updated++;
                }
            } else {
                gameCommandUseCase.createGame(
                    seed.name(),
                    seed.nameKo(),
                    seed.nameEn(),
                    seed.minPlayers(),
                    seed.maxPlayers(),
                    seed.scoreStrategy()
                );
                inserted++;
            }
        }

        if (updated > 0) {
            log.info("Updated {} official board games with localized names", updated);
        }

        if (inserted > 0) {
            log.info("Seeded {} official board games", inserted);
        }
    }

    private record SeedGame(
        String name,
        String nameKo,
        String nameEn,
        int minPlayers,
        int maxPlayers,
        ScoreStrategy scoreStrategy
    ) {}
}
