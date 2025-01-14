package bigsir.betterslimes.enums;

public enum FlowerColor {
	YELLOW(330, 4),
	RED(331, 14),
	PINK(332, 6),
	PURPLE(333, 10),
	LIGHTBLUE(334, 3),
	ORANGE(335, 1);
	public final int blockID;
	public final int dyeID;

	private FlowerColor(int blockID, int dyeID) {
		this.blockID = blockID;
		this.dyeID = dyeID;
	}
}
