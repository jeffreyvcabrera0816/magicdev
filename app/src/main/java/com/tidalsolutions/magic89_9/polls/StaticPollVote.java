package com.tidalsolutions.magic89_9.polls;

/**
 * Created by Jeffrey on 2/24/2016.
 */
public class StaticPollVote {

    private static String vote_id;

    public static void SetVoteID(String vote_id) {
        StaticPollVote.vote_id = vote_id;
    }

    public static String getVoteID () {
        return StaticPollVote.vote_id;
    }
}
