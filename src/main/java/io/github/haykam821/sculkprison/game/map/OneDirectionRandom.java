package io.github.haykam821.sculkprison.game.map;

import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSplitter;

public class OneDirectionRandom implements Random {
	private static final java.util.Random JAVA_RANDOM = new java.util.Random();
	private boolean enforceOneDirection = false;

	@Override
	public long nextLong() {
		return this.enforceOneDirection ? 0L : java.util.Random.from(JAVA_RANDOM).nextLong();
	}

	@Override
	public Random split() {
		return null;
	}

	@Override
	public RandomSplitter nextSplitter() {
		return null;
	}

	@Override
	public void setSeed(long seed) {

	}

	@Override
	public int nextInt() {
		return this.enforceOneDirection ? 0 : java.util.Random.from(JAVA_RANDOM).nextInt();
	}

	@Override
	public int nextInt(int bound) {
		return this.enforceOneDirection ? 0 : java.util.Random.from(JAVA_RANDOM).nextInt(bound);
	}

	@Override
	public double nextDouble() {
		return this.enforceOneDirection ? 0.0 : java.util.Random.from(JAVA_RANDOM).nextDouble();
	}

	@Override
	public float nextFloat() {
		return this.enforceOneDirection ? 0.0f : java.util.Random.from(JAVA_RANDOM).nextFloat();
	}

	@Override
	public boolean nextBoolean() {
		return this.enforceOneDirection ? false : java.util.Random.from(JAVA_RANDOM).nextBoolean();
	}

	@Override
	public double nextGaussian() {
		return this.enforceOneDirection ? 0.0 : java.util.Random.from(JAVA_RANDOM).nextGaussian();
	}

	public void enforceOneDirectionNext() {
		this.enforceOneDirection = true;
	}

	public void stopEnforcingOneDirection() {
		this.enforceOneDirection = false;
	}
}
