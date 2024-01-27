package me.funky.praxi.arena.generator;

import com.boydti.fawe.util.TaskManager;
import me.funky.praxi.Praxi;
import me.funky.praxi.arena.Arena;
import me.funky.praxi.arena.impl.SharedArena;
import java.io.File;
import java.util.concurrent.ThreadLocalRandom;
import lombok.AllArgsConstructor;
import me.funky.praxi.arena.ArenaType;
import me.funky.praxi.arena.impl.StandaloneArena;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.scheduler.BukkitRunnable;

@AllArgsConstructor
public class ArenaGenerator {

	private String name;
	private World world;
	private Schematic schematic;
	private ArenaType type;

	public void generate(File file, StandaloneArena parentArena) {
		log("Generating " + type.name() + " " + name + " arena...");

		int range = 500;
		int attempts = 0;

		int preciseX = ThreadLocalRandom.current().nextInt(range);
		int preciseZ = ThreadLocalRandom.current().nextInt(range);

		if (ThreadLocalRandom.current().nextBoolean()) {
			preciseX = -preciseX;
		}

		if (ThreadLocalRandom.current().nextBoolean()) {
			preciseZ = -preciseZ;
		}

		top:
		while (true) {
			attempts++;

			if (attempts >= 5) {
				preciseX = ThreadLocalRandom.current().nextInt(range);
				preciseZ = ThreadLocalRandom.current().nextInt(range);

				if (ThreadLocalRandom.current().nextBoolean()) {
					preciseX = -preciseX;
				}

				if (ThreadLocalRandom.current().nextBoolean()) {
					preciseZ = -preciseZ;
				}

				range += 500;

				log("Increased range to: " + range);
			}

			if (world.getBlockAt(preciseX, 72, preciseZ) == null) {
				continue;
			}

			int minX = preciseX - schematic.getClipBoard().getWidth() - 200;
			int maxX = preciseX + schematic.getClipBoard().getWidth() + 200;
			int minZ = preciseZ - schematic.getClipBoard().getLength() - 200;
			int maxZ = preciseZ + schematic.getClipBoard().getLength() + 200;
			int minY = 72;
			int maxY = 72 + schematic.getClipBoard().getHeight();

			for (int x = minX; x < maxX; x++) {
				for (int z = minZ; z < maxZ; z++) {
					for (int y = minY; y < maxY; y++) {
						if (world.getBlockAt(x, y, z).getType() != Material.AIR) {
							continue top;
						}
					}
				}
			}

			Location minCorner = new Location(world, minX, minY, minZ);
			Location maxCorner = new Location(world, maxX, maxY, maxZ);

			int finalPreciseX = preciseX;
			int finalPreciseZ = preciseZ;

			TaskManager.IMP.async(() -> {
				try {
					new Schematic(file).pasteSchematic(world, finalPreciseX, 76, finalPreciseZ);
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}

				Arena arena;

				if (type == ArenaType.STANDALONE) {
					arena = new StandaloneArena(name, minCorner, maxCorner);
				} else if (this.type == ArenaType.DUPLICATE) {
					arena = new Arena(name, minCorner, maxCorner);
					parentArena.getDuplicates().add(arena);
				} else {
					arena = new SharedArena(name, minCorner, maxCorner);
				}

				helper:
				for (int x = minX; x < maxX; x++) {
					for (int z = minZ; z < maxZ; z++) {
						for (int y = minY; y < maxY; y++) {
							if (world.getBlockAt(x, y, z).getType() == Material.SPONGE) {
								Block origin = world.getBlockAt(x, y, z);
								Block up = origin.getRelative(BlockFace.UP, 1);

								if (up.getState() instanceof Sign) {
									Sign sign = (Sign) up.getState();

									if (sign.getLine(0).isEmpty() || sign.getLine(1).isEmpty()) {
										continue;
									}

									float pitch = Float.valueOf(sign.getLine(0));
									float yaw = Float.valueOf(sign.getLine(1));
									Location loc = new Location(origin.getWorld(), origin.getX(), origin.getY(),
											origin.getZ(), yaw, pitch);

									new BukkitRunnable() {
										@Override
										public void run() {
											up.setType(Material.AIR);
											origin.setType(origin.getRelative(BlockFace.NORTH).getType());
										}
									}.runTask(Praxi.getInstance());

									if (arena.getSpawnA() == null) {
										arena.setSpawnA(loc);
									} else if (arena.getSpawnB() == null) {
										arena.setSpawnB(loc);
										break helper;
									}
								}
							}
						}
					}
				}

				Arena.getArenas().add(arena);

				if (type == ArenaType.STANDALONE) {
					for (int i = 0; i < 5; i++) {
						new BukkitRunnable() {
							@Override
							public void run() {
								System.out.println("Generating duplicate...");
								new ArenaGenerator(name, world, schematic, ArenaType.DUPLICATE)
										.generate(file, (StandaloneArena) arena);
							}
						}.runTask(Praxi.getInstance());
					}
				}
			});

			this.log(String.format("Pasted schematic at %1$s, %2$s, %3$s", preciseX, 76, preciseZ));

			break;
		}
	}

	private void log(String message) {
		Praxi.getInstance().getLogger().info("[ArenaGen] " + message);
	}

}
