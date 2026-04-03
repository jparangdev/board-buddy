-- Create custom_games table if not exists with public prefix
CREATE TABLE IF NOT EXISTS public.custom_games (
    id BIGSERIAL PRIMARY KEY,
    group_id BIGINT NOT NULL,
    name VARCHAR2(100) NOT NULL,
    min_players INT NOT NULL DEFAULT 1,
    max_players INT NOT NULL DEFAULT 10,
    score_strategy VARCHAR2(20) NOT NULL DEFAULT 'RANK_ONLY',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES public.groups(id) ON DELETE CASCADE,
    UNIQUE (group_id, name)
);

-- Add won column to game_results if not exists with public prefix
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema='public' AND table_name='game_results' AND column_name='won') THEN
        ALTER TABLE public.game_results ADD COLUMN won BOOLEAN NOT NULL DEFAULT FALSE;
    END IF;
END $$;

-- Add display_order column to group_members if not exists with public prefix
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema='public' AND table_name='group_members' AND column_name='display_order') THEN
        ALTER TABLE public.group_members ADD COLUMN display_order INT NOT NULL DEFAULT 0;
    END IF;
END $$;

-- Add name_ko and name_en columns to games table
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema='public' AND table_name='games' AND column_name='name_ko') THEN
        ALTER TABLE public.games ADD COLUMN name_ko VARCHAR2(100);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema='public' AND table_name='games' AND column_name='name_en') THEN
        ALTER TABLE public.games ADD COLUMN name_en VARCHAR2(100);
    END IF;
END $$;

-- Add name_ko and name_en columns to custom_games table
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema='public' AND table_name='custom_games' AND column_name='name_ko') THEN
        ALTER TABLE public.custom_games ADD COLUMN name_ko VARCHAR2(100);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema='public' AND table_name='custom_games' AND column_name='name_en') THEN
        ALTER TABLE public.custom_games ADD COLUMN name_en VARCHAR2(100);
    END IF;
END $$;
