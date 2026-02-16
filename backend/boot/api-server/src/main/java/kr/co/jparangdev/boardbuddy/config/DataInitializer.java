package kr.co.jparangdev.boardbuddy.config;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import kr.co.jparangdev.boardbuddy.application.game.usecase.GameCommandUseCase;
import kr.co.jparangdev.boardbuddy.application.game.usecase.GameQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final GameQueryUseCase gameQueryUseCase;
    private final GameCommandUseCase gameCommandUseCase;
    private final org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Checking final schema state...");
        try {
            boolean wonExists = jdbcTemplate.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema='public' AND table_name='game_results' AND column_name='won')",
                Boolean.class
            );
            log.info("Column 'won' in 'game_results' exists: {}", wonExists);

            boolean customGamesExists = jdbcTemplate.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema='public' AND table_name='custom_games')",
                Boolean.class
            );
            log.info("Table 'custom_games' exists: {}", customGamesExists);

            boolean displayOrderExists = jdbcTemplate.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema='public' AND table_name='group_members' AND column_name='display_order')",
                Boolean.class
            );
            log.info("Column 'display_order' in 'group_members' exists: {}", displayOrderExists);
        } catch (Exception e) {
            log.error("Failed to check final schema state: {}", e.getMessage());
        }

        var existingGames = gameQueryUseCase.getGameList();
        var existingNames = existingGames.stream()
                .map(g -> g.getName())
                .collect(java.util.stream.Collectors.toSet());

        List<SeedGame> seedGames = List.of(
            new SeedGame("Catan", "카탄", "Catan", 3, 4, "HIGH_WIN"),
            new SeedGame("Splendor", "스플렌더", "Splendor", 2, 4, "HIGH_WIN"),
            new SeedGame("Ticket to Ride", "티켓 투 라이드", "Ticket to Ride", 2, 5, "HIGH_WIN"),
            new SeedGame("Azul", "아줄", "Azul", 2, 4, "HIGH_WIN"),
            new SeedGame("7 Wonders", "7 원더스", "7 Wonders", 2, 7, "HIGH_WIN"),
            new SeedGame("Dominion", "도미니언", "Dominion", 2, 4, "HIGH_WIN"),
            new SeedGame("Codenames", "코드네임", "Codenames", 4, 8, "WIN_LOSE"),
            new SeedGame("Pandemic", "팬데믹", "Pandemic", 2, 4, "COOPERATIVE"),
            new SeedGame("The Resistance", "레지스탕스", "The Resistance", 5, 10, "WIN_LOSE"),
            new SeedGame("Uno", "우노", "Uno", 2, 10, "LOW_WIN"),
            new SeedGame("Love Letter", "러브레터", "Love Letter", 2, 6, "HIGH_WIN"),
            new SeedGame("Dixit", "딕싯", "Dixit", 3, 8, "HIGH_WIN"),
            new SeedGame("윷놀이", "윷놀이", "Yut Nori", 2, 4, "HIGH_WIN"),
            new SeedGame("할리갈리", "할리갈리", "Halli Galli", 2, 6, "RANK_ONLY"),
            new SeedGame("뱅!", "뱅!", "Bang!", 4, 7, "WIN_LOSE"),
            new SeedGame("루미큐브", "루미큐브", "Rummikub", 2, 4, "HIGH_WIN")
        );

        int inserted = 0;
        for (SeedGame seed : seedGames) {
            if (!existingNames.contains(seed.name())) {
                gameCommandUseCase.createGame(
                    seed.name(),
                    seed.nameKo(),
                    seed.nameEn(),
                    seed.minPlayers(),
                    seed.maxPlayers(),
                    kr.co.jparangdev.boardbuddy.domain.game.ScoreStrategy.valueOf(seed.scoreStrategy())
                );
                inserted++;
            }
        }

        if (inserted > 0) {
            log.info("Seeded {} official board games", inserted);
        }
    }

    private record SeedGame(String name, String nameKo, String nameEn, int minPlayers, int maxPlayers, String scoreStrategy) {}
}
