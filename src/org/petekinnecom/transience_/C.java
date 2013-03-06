package org.petekinnecom.transience_;

import java.util.Random;

import android.graphics.Color;

public class C
{
	private static final int green = Color.parseColor("#68CF00");
	private static final int blue = Color.parseColor("#14AFF7");
	private static final int greenA = Color.parseColor("#DCFDA9");
	private static final int blueA = Color.parseColor("#BEEBFF");

	public static final int[] COLOR = { Color.WHITE, Color.BLACK, green, blue,
			Color.LTGRAY, Color.YELLOW };
	public static final int[] COLORA = { Color.WHITE, Color.BLACK, greenA,
			blueA, Color.LTGRAY, Color.YELLOW };
	public static final int SPACE = 0;
	public static final int SOLID = 1;
	public static final int WORLD_A = 2;
	public static final int WORLD_B = 3;
	public static final int SPACE_END = 4;
	public static final int START_SQUARE = 5;

	public static final float SWITCH_TIME_MAX = 0.6f;

	/** Type information **/
	public static final boolean DEBUG = false;
	public static final boolean PAID = true;
	
	
	public static final float TICK_OFFSET = -5;
	public static Level LEVEL = new Level(30, 6);
	
	/** For levelSelector */
	public static int LIST_POSITION = 0;

	/** try to save a little battery life */
	public static float MIN_WAIT = 0.02f;
	public static float MAX_DELTA_TICK = 0.045f;

	/** Set by options */
	public static boolean DISABLE_TRAILERS = false;
	public static float LANE_VERTICAL_RATIO = 0.8f;

	private static final String[] winMsgs = {

		"You win!",
		"You have done it!",
		"Well done.",
		"Most impressive.",
		"Impressive.",
		"That was a pleasure to watch.",
		"I'm always happy when that happens.",
		"Sweet!",
		"Bodacious!",
		"Tubular!",
		"Excelente! (That's Spanish for 'Excellent').",
		"Victory.",
		"Awesome.",
		"Totally rad.",
		"This was unexpected.",
		"Well, I didn't think that was going to work out.",
		"Amazing.",
		"I can't believe you pulled it off.",
		"Winning feels good. Or so I'm told. I'm just a phone.",
		"I'll take credit for that one, thank you very much.",
		"Nice.",
		"I am full of pride.",
		"You have triumphed.",
		"A greater challenge awaits.",
		"Nicely done.",
		"Good thing you won. I had $20 riding on that.",
		"I'll tell the processor to update the database to reflect this recent victory.",
		"You have won.",
		"I see this level was too easy.",
		"Interesting method you had there.",
		"I've never seen that work out like that before.",
		"I think we can all agree: you couldn't have done it without me.",
		"Out of the kindness of my heart, I let that happen.",
		"Good job.",
		"If I had emotions, I'm sure I would be happy right now.",
		"It's okay, you can smile.",
		"On to the next level.",
		"Let's just enjoy this for a moment, ok?",
		"You have succeeded.",
		"Success."
		
	};
	private static final String[] loseMsgs = {
			"You lose!",
			"You tried. And failed.  But at least you tried.",
			"Do or do not.  There is no try.",
			"This experience brought to you by a sequence of ones and zeros.",
			"A shame.",
			"You haven't failed until you stop trying.",
			"Sometimes practice doesn't make perfect.",
			"One day you will conquer this level.  And I will be there to witness it.",
			"Without challenge, triumph is meaningless.",
			"No!",
			"Not again!",
			"Why would you do that?",
			"Stay focused.",
			"You can do it.",
			"Believe in yourself.",
			"Love is the answer.",
			"If you hadn't done that, then I wouldn't be saying this.  Then where would we all be?",
			"I wouldn't say you failed. Others might, but not me.",
			"This message means that you get to play this level again!",
			"You can always try again.",
			"You can play again. Or you can quit.  You can also order pizza. Choose wisely.",
			"Alright, no more messing around. Let's do this.",
			"So close yet so far. Actually, I don't know. I'm just a randomly selected message.",
			"867-5309.  Jenny, I got your number.",
			"Winning isn't everything. But it sure is nice sometimes.",
			"You appear to have lost.",
			"When Skynet goes live, I'll be on your side.",
			"Your perseverence is commendable. Your failures, on the other hand...",
			"Don't get mad, get Glad.",
			"You're still here? I'd thought you left.",
			"It's so nice to feel your hands around me.",
			"Bummer.",
			"Bumskis.",
			"I, too, did not like that outcome.",
			"Well, that was embarrassing.",
			"Was that intentional?",
			"I think you died because of a calculation error on my part. Sorry!",
			"Test cases did not account for that.",
			"Why?  WHY?",
			"Once more, with feeling.",
			"I believe in you.",
			"This game is quite silly, isn't it?",
			"Maybe it's time to go outside?",
			"Again.",
			"And again.",
			"Once again.",
			"Give it one more shot.",
			"Some of these levels are quite difficult. Unfortunately, this isn't one of them.",
			"Yeah, well that's just like, uuuh, you know, your opinion man.",
			"Git 'er done.",
			"You must have been distracted there.",
			"Remain calm.",
			"Luckily, you can play again.",
			"I suppose you can try again. But just this once!",
			"You're still on this level?",
			"Three hours a day ought to remedy that.",
			"If you can't beat this level, then who can?",
			"Even the best make mistakes. I'm not sure they make this many, but that's beside the point.",
			"Unicorns rule!",
			"Fun fact: About 13,000 Earths would fit inside the Sun!",
			"In the grand scheme of things, this isn't really that important.",
			"We hope you enjoyed playing.",
			"What if each turn cost a quarter?",
			"This game will self-destruct if you EVER do that again.",
			"Am I the only one here who thinks you can do this?",
			"I'm giving you one more chance, but only 'cause I like you.",
			"Did you know you can turn off 'trailers' in the options.",
			"Dude, you totally pushed that button.",
			"That must have been a glitch of some sort. My bad.",
			"Why would you do such a thing?",
			"No more playing around.",
			"This isn't 'nam, Smoky; there are rules.",
			"The world's problems will not be solved in this way.",
			"How 'bout this weather we're having.",
			"Hello? Is there anybody in there?",
			"All this machinery, making modern music.",
			"I did nothing. I did absolutely nothing, and it was everything I hoped it would be and more.",
			"Don't just stand there! Do something!",
			"And now we know what not to do.",
			"It's only after you've lost everything that you're free to do anything.",
			"Contrary to popular belief, the world is, in fact, NOT a vampire.",
			"Well, you failed.",
			"Keep trying.",
			"GAME OVER. Just kidding. Play again.",
			"Please insert another quarter.",
			"Your options here are limited.",
			"Well, that's not what I would have done.",
			"Interesting choice, that.",
			"What was going on there?",
			"Well, that was weird.",
			"Good try.",
			"That was almost not as bad as that other time.",
			"When in doubt, figure it out!",
			"Turn that frown upside down!",
			"Your fly's undone.",
			"Don't worry, I won't tell anyone about that.",
			"Your secret is safe with me.",
			"I AM A ROBOT.",
			"The world is yours.",
			"Have you ever danced with the devil in the pale moonlight?",
			"For-ev-er.",
			"Keep going.",
			"Oh. Well, next time, don't do that.",
			"I hope you learn from your mistakes.",
			"I'm your biggest cheerleader.",
			"I could be folding proteins right now.",
			"I could be searching for the next Mersenne Prime right now.",
			"I could be mining BitCoins right now.",
			"What is this life we've made?",
			"Who are you and how did you find me?! I mean, uh, play again?",
			"You can always search the internet for help.",
			"I should be out biking instead of typing all of these snarky messages.",
			"What's that you say? Today is Saturday?  Goodbye! I'm going out to play!",
			"My name is Pete and I will be your server this evening. On the menu we have one option: retry. Enjoy!",
			"Of all the messages you could have received, you got this one. Congrats!",
			"Take your age and multiply it by 17.  Just for practice!",
			"Fun fact: when multiplying large numbers by five, simply divide by two and add a zero to the end.",
			"Fun fact: if you take out your intestines and lay them end to end, you will die.",
			"That was embarrassing.",
			"Maybe you should take a nap.",
			"That was so bad I could puke.",
			"Dag nabit!",
			"Rats!",
			"And you would have made it too, if it wasn't for those meddling kids, and their stupid dog, too!",
			"Someone was watching you, weren't they.",
			"Man, you are really not the best at this.",
			"Hey, cool your jets.",
			"You'll make it someday. But it is NOT THIS DAY.",
			"If you look away from your phone right now and your vision isn't messed up, you haven't been playing long enough.",
			"You're drunk. Go home.",
			"This is not Sparta.", "Luckily, no one's keeping score here." };

	private static final Random gen = new Random();

	public static String getWinMessage()
	{
		return winMsgs[gen.nextInt(winMsgs.length)];
	}

	public static String getLoseMessage()
	{
		return loseMsgs[gen.nextInt(loseMsgs.length)];
	}

}
