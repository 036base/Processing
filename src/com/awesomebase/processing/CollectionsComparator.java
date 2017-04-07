package com.awesomebase.processing;

import java.util.Comparator;

/**
 * Collections Comparator
 *
 * @author
 *
 */
public class CollectionsComparator {

	/**
	 * UIDによるソート用比較クラス
	 */
	public class UidComparator implements Comparator<AnimatedImage> {
		@Override
		public int compare(AnimatedImage p1, AnimatedImage p2) {
			return Long.compare(p1.getUid(), p2.getUid());

		}
	}

	/**
	 * 倍率によるソート用比較クラス
	 */
	public class ScaleComparator implements Comparator<AnimatedImage> {
		@Override
		public int compare(AnimatedImage p1, AnimatedImage p2) {
			return Float.compare(p1.getScale(), p2.getScale());

		}
	}

}
