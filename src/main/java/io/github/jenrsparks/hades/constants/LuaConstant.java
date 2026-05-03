package io.github.jenrsparks.hades.constants;

public enum LuaConstant {
    // Get into the data block at the top-level
    LUA_DATA_KEY ("LUA_DATA"),
    COMPLETED_RUNS_KEY ("COMPLETED_RUNS"),
    
    // // Current / latest state:
    // // -- Total time played
    // TIME_PLAYED_KEY ("GameState/TotalTime"),
    // // BiomeTotalTimes / 

    // // -- Most recent pinned keepsake
    // PINNED_KEEPSAKE_KEY ("SaveFirstKeepsakeName"),
    
    // // Historic deep-dive
    // // -- Full record or all runs
    // RUN_HISTORY_KEY ("runHistory"),

    // // Summary stats:
    // // -- How much each hero has damaged the player
    // HERO_DMG_DEALT_KEY ("DamageDealtByHeroRecord"),
    // // -- How much each enemy has damaged the player
    // ENEMY_DMG_DEALT_KEY ("DamageTakenFromRecord"),
    // // -- How many times each enemy has spawned
    // ENEMY_SPAWN_KEY ("SpawnRecord"),
    // // -- How many times each enemy has been killed
    // ENEMY_KILLS_KEY ("EnemyKills"),
    // // -- How many times Nemisis has stolen a given door type
    // NEM_STOLEN_DOORS_KEY ("NemesisTakeExitRecord"),
    // // -- How many times Heacate has polymorphed the player to a given creature type
    // HECATE_POLY_KEY ("PolymorphRecord"),

    ;

        private final String fieldName;
        
        LuaConstant(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getFieldName() {
            return fieldName;
        }

}
