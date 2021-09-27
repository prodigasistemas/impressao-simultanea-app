package util;

import java.io.IOException;

public class GZIP {

	private static final int FHCRC_MASK = 2;
	private static final int FEXTRA_MASK = 4;
	private static final int FNAME_MASK = 8;
	private static final int FCOMMENT_MASK = 16;

	private static final int BTYPE_NONE = 0;
	private static final int BTYPE_DYNAMIC = 2;

	private static final int MAX_BITS = 16;
	private static final int MAX_CODE_LITERALS = 287;
	private static final int MAX_CODE_DISTANCES = 31;
	private static final int MAX_CODE_LENGTHS = 18;
	private static final int EOB_CODE = 256;

	private static final int LENGTH_EXTRA_BITS[] = { 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 0, 99, 99 };
	private static final int LENGTH_VALUES[] = { 3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 15, 17, 19, 23, 27, 31, 35, 43, 51, 59, 67, 83, 99, 115, 131, 163, 195, 227, 258, 0, 0 };
	private static final int DISTANCE_EXTRA_BITS[] = { 0, 0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12, 13, 13 };
	private static final int DISTANCE_VALUES[] = { 1, 2, 3, 4, 5, 7, 9, 13, 17, 25, 33, 49, 65, 97, 129, 193, 257, 385, 513, 769, 1025, 1537, 2049, 3073, 4097, 6145, 8193, 12289, 16385, 24577 };
	private static final int DYNAMIC_LENGTH_ORDER[] = { 16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15 };


	private static int gzipIndex, gzipByte, gzipBit;

	public static byte[] inflate(byte gzip[]) throws IOException {
		gzipIndex = gzipByte = gzipBit = 0;

		if (readBits(gzip, 16) != 0x8B1F || readBits(gzip, 8) != 8)
			throw new IOException("Invalid GZIP format");

		int flg = readBits(gzip, 8);

		gzipIndex += 6;

		if ((flg & FEXTRA_MASK) != 0)
			gzipIndex += readBits(gzip, 16);
		if ((flg & FNAME_MASK) != 0)
			while (gzip[gzipIndex++] != 0)
				;
		if ((flg & FCOMMENT_MASK) != 0)
			while (gzip[gzipIndex++] != 0)
				;
		if ((flg & FHCRC_MASK) != 0)
			gzipIndex += 2;

		int index = gzipIndex;
		gzipIndex = gzip.length - 4;
		byte uncompressed[] = new byte[readBits(gzip, 16) | (readBits(gzip, 16) << 16)];
		int uncompressedIndex = 0;
		gzipIndex = index;

		int bfinal = 0, btype = 0;
		do {

			bfinal = readBits(gzip, 1);
			btype = readBits(gzip, 2);

			if (btype == BTYPE_NONE) {

				gzipBit = 0;

				int len = readBits(gzip, 16);

				System.arraycopy(gzip, gzipIndex, uncompressed, uncompressedIndex, len);
				gzipIndex += len;

				uncompressedIndex += len;
			} else {
				int literalTree[], distanceTree[];
				if (btype == BTYPE_DYNAMIC) {

					int hlit = readBits(gzip, 5) + 257;
					int hdist = readBits(gzip, 5) + 1;
					int hclen = readBits(gzip, 4) + 4;

					byte lengthBits[] = new byte[MAX_CODE_LENGTHS + 1];
					for (int i = 0; i < hclen; i++)
						lengthBits[DYNAMIC_LENGTH_ORDER[i]] = (byte) readBits(gzip, 3);

					int lengthTree[] = createHuffmanTree(lengthBits, MAX_CODE_LENGTHS);

					literalTree = createHuffmanTree(decodeCodeLengths(gzip, lengthTree, hlit), hlit - 1);
					distanceTree = createHuffmanTree(decodeCodeLengths(gzip, lengthTree, hdist), hdist - 1);
				} else {
					byte literalBits[] = new byte[MAX_CODE_LITERALS + 1];
					for (int i = 0; i < 144; i++)
						literalBits[i] = 8;
					for (int i = 144; i < 256; i++)
						literalBits[i] = 9;
					for (int i = 256; i < 280; i++)
						literalBits[i] = 7;
					for (int i = 280; i < 288; i++)
						literalBits[i] = 8;
					literalTree = createHuffmanTree(literalBits, MAX_CODE_LITERALS);

					byte distanceBits[] = new byte[MAX_CODE_DISTANCES + 1];
					for (int i = 0; i < distanceBits.length; i++)
						distanceBits[i] = 5;
					distanceTree = createHuffmanTree(distanceBits, MAX_CODE_DISTANCES);
				}

				int code = 0, leb = 0, deb = 0;
				while ((code = readCode(gzip, literalTree)) != EOB_CODE) {
					if (code > EOB_CODE) {
						code -= 257;
						int length = LENGTH_VALUES[code];
						if ((leb = LENGTH_EXTRA_BITS[code]) > 0)
							length += readBits(gzip, leb);
						code = readCode(gzip, distanceTree);
						int distance = DISTANCE_VALUES[code];
						if ((deb = DISTANCE_EXTRA_BITS[code]) > 0) {
							distance += readBits(gzip, deb);
						}

						int offset = uncompressedIndex - distance;
						while (distance < length) {
							System.arraycopy(uncompressed, offset, uncompressed, uncompressedIndex, distance);
							uncompressedIndex += distance;
							length -= distance;
							distance <<= 1;
						}
						
						System.arraycopy(uncompressed, offset, uncompressed, uncompressedIndex, length);
						uncompressedIndex += length;
					} else {
						uncompressed[uncompressedIndex++] = (byte) code;
					}
				}
			}
		} while (bfinal == 0);

		return uncompressed;
	}

	private static int readBits(byte gzip[], int n) {
		int data = (gzipBit == 0 ? (gzipByte = (gzip[gzipIndex++] & 0xFF)) : (gzipByte >> gzipBit));

		for (int i = (8 - gzipBit); i < n; i += 8) {
			gzipByte = (gzip[gzipIndex++] & 0xFF);
			data |= (gzipByte << i);
		}
		
		gzipBit = (gzipBit + n) & 7;
		
		return (data & ((1 << n) - 1));
	}

	private static int readCode(byte gzip[], int tree[]) {
		int node = tree[0];
		while (node >= 0) {
			if (gzipBit == 0)
				gzipByte = (gzip[gzipIndex++] & 0xFF);
			node = (((gzipByte & (1 << gzipBit)) == 0) ? tree[node >> 16] : tree[node & 0xFFFF]);
			gzipBit = (gzipBit + 1) & 7;
		}
		return (node & 0xFFFF);
	}

	private static byte[] decodeCodeLengths(byte gzip[], int lengthTree[], int count) {
		byte bits[] = new byte[count];
		for (int i = 0, code = 0, last = 0; i < count;) {
			code = readCode(gzip, lengthTree);
			if (code >= 16) {
				int repeat = 0;
				if (code == 16) {
					repeat = 3 + readBits(gzip, 2);
					code = last;
				} else {
					if (code == 17)
						repeat = 3 + readBits(gzip, 3);
					else
						repeat = 11 + readBits(gzip, 7);
					code = 0;
				}
				while (repeat-- > 0)
					bits[i++] = (byte) code;
			} else
				bits[i++] = (byte) code;

			last = code;
		}
		return bits;
	}

	private static int[] createHuffmanTree(byte bits[], int maxCode) {
		int bl_count[] = new int[MAX_BITS + 1];
		
		for (int i = 0; i < bits.length; i++)
			bl_count[bits[i]]++;
		
		int code = 0;
		bl_count[0] = 0;
		int next_code[] = new int[MAX_BITS + 1];
		for (int i = 1; i <= MAX_BITS; i++) next_code[i] = code = (code + bl_count[i - 1]) << 1;
		
		int tree[] = new int[(maxCode << 1) + MAX_BITS];
		int treeInsert = 1;
		
		for (int i = 0; i <= maxCode; i++) {
			int len = bits[i];
			if (len != 0) {
				code = next_code[len]++;
				int node = 0;
				for (int bit = len - 1; bit >= 0; bit--) {
					int value = code & (1 << bit);
					if (value == 0) {
						int left = tree[node] >> 16;
						if (left == 0) {
							tree[node] |= (treeInsert << 16);
							node = treeInsert++;
						} else {
							node = left;
						}
					} else {
						int right = tree[node] & 0xFFFF;
						if (right == 0) {
							tree[node] |= treeInsert;
							node = treeInsert++;
						} else {
							node = right;
						}
					}
				}
				
				tree[node] = 0x80000000 | i;
			}
		}

		return tree;
	}
}