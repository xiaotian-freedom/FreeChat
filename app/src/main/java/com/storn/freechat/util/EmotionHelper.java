package com.storn.freechat.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;

import com.storn.freechat.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author GoForward
 *
 */
public class EmotionHelper {
	public static final String CORRECT_SYNBOL = " ";
	public static final String DELETE_SYNBOL = "delete";
	private static final int ONE_PAGE_SIZE = 20;
	public static List<List<String>> emojiGroups;
	private static Pattern pattern;
	private static String[] emojiCodes = new String[]{
		"weixiao",
		"kaixin",
		"keai",
		"daxiao",
		"touxiao",
		"diaopi",
		"jianqianyakai",
		"qinqin",
		"se",
		"xiaokule",
		"haixiu",
		"bizui",
		"dai",
		"deyi",
		"han",
		"heixian",
		"heng",
		"jiujie",
		"ku",
		"kun",
		"shihua",
		"shuijue",
		"weiqu",
		"yihuo",
		"yun",
		"tu",
		"he",
		"zhamaole",
		"nu",
		"xianhua",
		"fen",
		"dou",
		"shuai",
		"dabai"};

	public static String[] emojiCodesSend = new String[]{
		"[微笑]",
		"[开心]",
		"[可爱]",
		"[大笑]",
		"[偷笑]",
		"[调皮]",
		"[捡钱乐]",
		"[亲亲]",
		"[色]",
		"[笑哭了]",
		"[害羞]",
		"[闭嘴]",
		"[呆]",
		"[得意]",
		"[汗]",
		"[黑线]",
		"[哼]",
		"[纠结]",
		"[哭]",
		"[困]",
		"[石化]",
		"[睡觉]",
		"[委屈]",
		"[疑惑]",
		"[晕]",
		"[吐]",
		"[吓]",
		"[炸毛了]",
		"[怒]",
		"[献花]",
		"[奋]",
		"[斗]",
		"[帅]",
		"[大白]"};

	static {
		int pages = emojiCodesSend.length / ONE_PAGE_SIZE
				+ (emojiCodesSend.length % ONE_PAGE_SIZE == 0 ? 0 : 1);
		emojiGroups = new ArrayList<List<String>>();
		for (int page = 0; page < pages; page++) {
			List<String> onePageEmojis = new ArrayList<String>();
			int expectNum = page * ONE_PAGE_SIZE + ONE_PAGE_SIZE;
			int start = page * ONE_PAGE_SIZE;
			int end = Math.min(expectNum, emojiCodesSend.length);
			for (int i = start; i < end; i++) {
				onePageEmojis.add(emojiCodesSend[i]);
				if (i == end - 1 && end == expectNum) {
					onePageEmojis.add(DELETE_SYNBOL);
				}
				if (i == end - 1 && end < expectNum) {
					for (int j = end; j < expectNum; j++) {
						onePageEmojis.add(" ");
						if (j == expectNum - 1) {
							onePageEmojis.add(DELETE_SYNBOL);
						}
					}
				}
			}
			emojiGroups.add(onePageEmojis);
		}
		pattern = Pattern.compile("\\[[\u4e00-\u9fa5]+\\]");
	}

	public static int contain(String[] strings, String string) {
		int positon = -1;
		for (int i = 0; i < strings.length; i++) {
			if (strings[i].equals(string)) {
				positon = i;
				break;
			}
		}
		return positon;
	}

	public static CharSequence replace(Context context, String text,
			int inSampleSize, boolean needCorrect) {
		if (TextUtils.isEmpty(text)) {
			return text;
		}
		if (needCorrect) {
			text = text.replace("]", "] ");
		}
		SpannableStringBuilder spannableString = new SpannableStringBuilder(
				text);
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			String factText = matcher.group();
			// String key = factText.substring(1, factText.length() - 1);
			int position = contain(emojiCodesSend, factText);
			if (position >= 0) {
				// Bitmap bitmap = getEmojiDrawable(context,
				// emojiCodes[position], inSampleSize);
				// ImageSpan image = new ImageSpan(context, bitmap);
				int icoSize = context
				.getResources()
				.getDimensionPixelOffset(R.dimen.chat_emotion_size);
				Drawable drawable = getDrawableByName(context,
						emojiCodes[position]);
				drawable.setBounds(0, 0, icoSize, icoSize);// 这里设置图片的大小
				ImageSpan imageSpan = new ImageSpan(drawable);
				int start = matcher.start();
				int end = matcher.end();
				spannableString.setSpan(imageSpan, start, end,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		return spannableString;
	}

	public static Drawable getDrawableByName(Context context, String name) {
		Drawable drawable = context.getResources().getDrawable(
				context.getResources().getIdentifier("emotion_" + name,
						"drawable", context.getPackageName()));
		return drawable;
	}

	public static void isEmojiDrawableAvailable(Context context, int inSampleSize) {
		for (String emojiCode : emojiCodes) {
			String code = emojiCode.substring(1, emojiCode.length() - 1);
			Bitmap bitmap = getDrawableByName(context, code,
					inSampleSize);
		}
	}

	public static Bitmap getEmojiDrawableForAdapter(Context context,
			String nameZH) {
		int position = contain(emojiCodesSend, nameZH);
		if (position < 0) {
			return null;
		}
		return getEmojiDrawable(context, emojiCodes[position], 1);
	}

	public static Bitmap getEmojiDrawable(Context context, String name) {
		return getEmojiDrawable(context, name, 1);
	}

	public static Bitmap getEmojiDrawable(Context context, String name,
			int inSampleSize) {
		return getDrawableByName(context, "emotion_" + name, inSampleSize);
	}

	public static Bitmap getDrawableByName(Context ctx, String name,
			int inSampleSize) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = inSampleSize;
		Bitmap bitmap = BitmapFactory.decodeResource(
				ctx.getResources(),
				ctx.getResources().getIdentifier(name, "drawable",
						ctx.getPackageName()), options);
		return bitmap;
	}

}
