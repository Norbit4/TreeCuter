package pl.norbit.treecuter.jobs;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.BlockActionInfo;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.JobsPlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class JobsService {
    public static void updateJobs(Player p, Block block){
        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(p);
        if(jPlayer != null) Jobs.action(jPlayer, new BlockActionInfo(block, ActionType.BREAK), block);
    }
}
