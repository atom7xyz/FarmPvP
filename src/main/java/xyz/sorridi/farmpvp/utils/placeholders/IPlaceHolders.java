package xyz.sorridi.farmpvp.utils.placeholders;

import xyz.sorridi.stone.utils.data.Array;

import java.util.List;

public interface IPlaceHolders
{
    List<String> EMPTY_LIST = List.of();
    String EMPTY_STRING = "";
    String CONSOLE_NAME = "Il server";

    String NAME     = "{name}";
    String USER     = "{user}";
    String TOTAL    = "{tot}";
    String STAFFER  = "{staffer}";
    String TIME     = "{time}";
    String TARGET   = "{target}";
    String PERM     = "{perm}";
    String TOT_BNT  = "{tot_bounties}";
    String FROM     = "{from}";
    String VICTIM   = "{victim}";
    String AT       = "{at}";
    String GAMEMODE = "{gamemode}";
    String POINTS   = "{points}";
    String LEVELS   = "{levels}";
    String COINS    = "{coins}";
    String MAX      = "{max}";
    String MIN      = "{min}";
    String MESSAGE  = "{message}";
    String OWNER    = "{owner}";
    String SIZE     = "{size}";
    String MEMBERS  = "{members}";
    String X        = "{x}";
    String Y        = "{y}";
    String Z        = "{z}";
    String COLOR    = "{color}";
    String NEXT_LVL = "{next_level}";
    String P_KILLS  = "{player_kills}";
    String M_KILLS  = "{mob_kills}";
    String DEATHS   = "{deaths}";
    String KD       = "{kd}";
    String PPS      = "{pps}";
    String NEXT_PPS = "{next_pps}";
    String NEW_PTS  = "{new_points}";
    String DRAW_IN  = "{draw_in}";
    String NEED_PTS = "{need_points}";
    String COST     = "{cost}";
    String TYPE     = "{type}";

    String DEBUG_PERM = "farmpvp.debug";
    String ERROR_MESSAGE = "An error occurred from " + FROM + " while forgetting " + NAME + ".";
    String FORGET_MESSAGE = "Forgot " + NAME + " from " + FROM + ".";

    String[] NAME_FROM = Array.of(NAME, FROM);

    String[] TARGET_TOT         = Array.of(TARGET, TOTAL);
    String[] TARGET_GAMEMODE    = Array.of(TARGET, GAMEMODE);
    String[] TARGET_STAFFER     = Array.of(TARGET, STAFFER);
    String[] TARGET_PTS         = Array.of(TARGET, POINTS);
    String[] TARGET_LVLS        = Array.of(TARGET, LEVELS);
    String[] TARGET_COINS       = Array.of(TARGET, COINS);

    String[] TOT_TARGET_PTS     = Array.of(TOTAL, TARGET, POINTS);
    String[] TOT_STAFF_PTS      = Array.of(TOTAL, STAFFER, POINTS);
    String[] TOT_TARGET_LVLS    = Array.of(TOTAL, TARGET, LEVELS);
    String[] TOT_STAFF_LVLS     = Array.of(TOTAL, STAFFER, LEVELS);
    String[] TOT_TARGET_COINS   = Array.of(TOTAL, TARGET, COINS);
    String[] TOT_STAFF_COINS    = Array.of(TOTAL, STAFFER, COINS);

    String[] USER_MESSAGE = Array.of(USER, MESSAGE);
    String[] USER_OWNER = Array.of(USER, OWNER);
    String[] TEAM_INFO = Array.of(OWNER, SIZE, MEMBERS);

    String[] XYZ = Array.of(X, Y, Z);
    String[] COLOR_LEVELS = Array.of(COLOR, LEVELS);

    Integer MIN_LEVELS = 0;
    Integer MAX_LEVELS = Integer.MAX_VALUE;

    Double MIN_POINTS = 0.0;
    Double MAX_POINTS = Double.MAX_VALUE;

    Integer MIN_COINS = 0;
    Integer MAX_COINS = Integer.MAX_VALUE;

    String[] MIN_MAX = Array.of(MIN, MAX);

    int ONE_CLOCK = 20;
    int HALF_CLOCK = ONE_CLOCK / 2;
    int QUARTER_CLOCK = HALF_CLOCK / 2;
    
    String[] POINTS_NEW_POINTS  = Array.of(POINTS, NEW_PTS);
    String[] LEVELS_NEXT_LEVEL  = Array.of(LEVELS, NEXT_LVL);
    String[] PPS_NEXT_PPS       = Array.of(PPS, NEXT_PPS);
    String[] LEVELS_POINTS      = Array.of(LEVELS, POINTS);
    String[] KILLS_DEATHS_KDR   = Array.of(P_KILLS, DEATHS, KD);
    String[] COST_TYPE          = Array.of(COST, TYPE);
}
