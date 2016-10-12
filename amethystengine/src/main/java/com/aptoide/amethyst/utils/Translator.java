package com.aptoide.amethyst.utils;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.R;

/**
 * Created by pedroribeiro on 02/05/16.
 */
public class Translator {

    public static String translate(String string) {
        if (string == null) {
            return string;
        }
        String translated = null;
        switch (string) {
            case "Home":
                translated = Aptoide.getContext().getString(R.string.home_title);
                break;
            case "Updates":
                translated = Aptoide.getContext().getString(R.string.updates);
                break;
            case "Downloads":
                translated = Aptoide.getContext().getString(R.string.downloads);
                break;
            case "Latest Reviews":
                translated = Aptoide.getContext().getString(R.string.latest_reviews);
                break;
            case "Latest Comments":
                translated = Aptoide.getContext().getString(R.string.latest_comments);
                break;
            case "Applications":
                translated = Aptoide.getContext().getString(R.string.applications);
                break;
            case "Games":
                translated = Aptoide.getContext().getString(R.string.games);
                break;
            case "Highlighted":
                translated = Aptoide.getContext().getString(R.string.highlighted);
                break;
            case "Trending":
                translated = Aptoide.getContext().getString(R.string.trending);
                break;
            case "Local Top Apps":
                translated = Aptoide.getContext().getString(R.string.local_top_apps);
                break;
            case "Top Games":
                translated = Aptoide.getContext().getString(R.string.top_games);
                break;
            case "Reviews":
                translated = Aptoide.getContext().getString(R.string.reviews);
                break;
            case "News & Weather":
                translated = Aptoide.getContext().getString(R.string.news_weather);
                break;
            case "Productivity":
                translated = Aptoide.getContext().getString(R.string.productivity);
                break;
            case "News & Magazines":
                translated = Aptoide.getContext().getString(R.string.news_magazines);
                break;
            case "Reference":
                translated = Aptoide.getContext().getString(R.string.reference);
                break;
            case "Shopping":
                translated = Aptoide.getContext().getString(R.string.shopping);
                break;
            case "Social":
                translated = Aptoide.getContext().getString(R.string.social);
                break;
            case "Business":
                translated = Aptoide.getContext().getString(R.string.business);
                break;
            case "Sports":
                translated = Aptoide.getContext().getString(R.string.sports);
                break;
            case "Themes":
                translated = Aptoide.getContext().getString(R.string.themes);
                break;
            case "Tools":
                translated = Aptoide.getContext().getString(R.string.tools);
                break;
            case "Travel":
                translated = Aptoide.getContext().getString(R.string.travel);
                break;
            case "Software Libraries":
                translated = Aptoide.getContext().getString(R.string.software_libraries);
                break;
            case "Demo":
                translated = Aptoide.getContext().getString(R.string.demo);
                break;
            case "Comics":
                translated = Aptoide.getContext().getString(R.string.comics);
                break;
            case "Music & Audio":
                translated = Aptoide.getContext().getString(R.string.music_audio);
                break;
            case "Weather":
                translated = Aptoide.getContext().getString(R.string.weather);
                break;
            case "Photography":
                translated = Aptoide.getContext().getString(R.string.photography);
                break;
            case "Communication":
                translated = Aptoide.getContext().getString(R.string.communication);
                break;
            case "Personalization":
                translated = Aptoide.getContext().getString(R.string.personalization);
                break;
            case "Travel & Local":
                translated = Aptoide.getContext().getString(R.string.travel_local);
                break;
            case "Transportation":
                translated = Aptoide.getContext().getString(R.string.transportation);
                break;
            case "Medical":
                translated = Aptoide.getContext().getString(R.string.medical);
                break;
            case "Entertainment":
                translated = Aptoide.getContext().getString(R.string.entertainment);
                break;
            case "Finance":
                translated = Aptoide.getContext().getString(R.string.finance);
                break;
            case "Health":
                translated = Aptoide.getContext().getString(R.string.health);
                break;
            case "Libraries & Demo":
                translated = Aptoide.getContext().getString(R.string.libraries_demo);
                break;
            case "Books & Reference":
                translated = Aptoide.getContext().getString(R.string.books_reference);
                break;
            case "Lifestyle":
                translated = Aptoide.getContext().getString(R.string.lifestyle);
                break;
            case "Transport":
                translated = Aptoide.getContext().getString(R.string.transport);
                break;
            case "Health & Fitness":
                translated = Aptoide.getContext().getString(R.string.health_fitness);
                break;
            case "Media & Video":
                translated = Aptoide.getContext().getString(R.string.media_video);
                break;
            case "Multimedia":
                translated = Aptoide.getContext().getString(R.string.multimedia);
                break;
            case "Education":
                translated = Aptoide.getContext().getString(R.string.education);
                break;
            case "All":
                translated = Aptoide.getContext().getString(R.string.all);
                break;
            case "Puzzle":
                translated = Aptoide.getContext().getString(R.string.puzzle);
                break;
            case "Casino":
                translated = Aptoide.getContext().getString(R.string.casino);
                break;
            case "Action":
                translated = Aptoide.getContext().getString(R.string.action);
                break;
            case "Strategy":
                translated = Aptoide.getContext().getString(R.string.strategy);
                break;
            case "Family":
                translated = Aptoide.getContext().getString(R.string.family);
                break;
            case "Simulation":
                translated = Aptoide.getContext().getString(R.string.simulation);
                break;
            case "Adventure":
                translated = Aptoide.getContext().getString(R.string.adventure);
                break;
            case "Word":
                translated = Aptoide.getContext().getString(R.string.word);
                break;
            case "Arcade":
                translated = Aptoide.getContext().getString(R.string.arcade);
                break;
            case "Arcade & Action":
                translated = Aptoide.getContext().getString(R.string.arcade_action);
                break;
            case "Trivia":
                translated = Aptoide.getContext().getString(R.string.trivia);
                break;
            case "Card":
                translated = Aptoide.getContext().getString(R.string.card);
                break;
            case "Role Playing":
                translated = Aptoide.getContext().getString(R.string.role_playing);
                break;
            case "Educational":
                translated = Aptoide.getContext().getString(R.string.educational);
                break;
            case "Music":
                translated = Aptoide.getContext().getString(R.string.music);
                break;
            case "Board":
                translated = Aptoide.getContext().getString(R.string.board);
                break;
            case "Brain & Puzzle":
                translated = Aptoide.getContext().getString(R.string.brain_puzzle);
                break;
            case "Cards & Casino":
                translated = Aptoide.getContext().getString(R.string.cards_casino);
                break;
            case "Casual":
                translated = Aptoide.getContext().getString(R.string.casual);
                break;
            case "Sports Games":
                translated = Aptoide.getContext().getString(R.string.sports_games);
                break;
            case "Racing":
                translated = Aptoide.getContext().getString(R.string.racing);
                break;
            case "Top Apps":
                translated = Aptoide.getContext().getString(R.string.top_apps);
                break;
            case "Latest Applications":
                translated = Aptoide.getContext().getString(R.string.latest_applications);
                break;
            case "Top Apps in this store":
                translated = Aptoide.getContext().getString(R.string.top_apps_in_store);
                break;
            case "Apps for Kids":
                translated = Aptoide.getContext().getString(R.string.apps_for_kids);
                break;
            case "Aptoide Publishers":
                translated = Aptoide.getContext().getString(R.string.aptoide_publishers);
                break;
            case "Music & Video":
                translated = Aptoide.getContext().getString(R.string.music_video);
                break;
            case "Essential Apps":
                translated = Aptoide.getContext().getString(R.string.essential_apps);
                break;
            case "Summer Apps":
                translated = Aptoide.getContext().getString(R.string.summer_apps);
                break;
            case "Play-it!":
                translated = Aptoide.getContext().getString(R.string.play_it);
                break;
            case "More Editors Choice":
                translated = Aptoide.getContext().getString(R.string.more_editors_choice);
                break;
            case "Beauty":
                translated = Aptoide.getContext().getString(R.string.beauty);
                break;
            case "Art & Design":
                translated = Aptoide.getContext().getString(R.string.art_design);
                break;
            case "House & Home":
                translated = Aptoide.getContext().getString(R.string.house_home);
                break;
            case "Food & Drink":
                translated = Aptoide.getContext().getString(R.string.food_drink);
                break;
            case "Auto & Vehicles":
                translated = Aptoide.getContext().getString(R.string.auto_vehicle);
                break;
            case "Maps & Navigation":
                translated = Aptoide.getContext().getString(R.string.maps_navigation);
                break;
            case "Video Players & Editors":
                translated = Aptoide.getContext().getString(R.string.video_players_editors);
                break;
            default:
                translated = string;
                break;
        }
        return translated;
    }

}
