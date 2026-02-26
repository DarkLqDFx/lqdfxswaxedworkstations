package net.lqdfxnet.waxedworkstations;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(LqDFxsWaxedWorkstations.MOD_ID)
public class LqDFxsWaxedWorkstations {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "lqdfxswaxedworkstations";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
}

