package net.spacetivity.survival.core.rank

import net.spacetivity.survival.core.land.LandFlag

data class Rank(val name: String, var id: Int, val format: RankFormat, val permissions: MutableList<String>, val flags: MutableList<LandFlag>) {



}