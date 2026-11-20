package com.example;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import java.io.*;
import java.util.*;
import javax.sound.sampled.*;

// declares a class for the app
public class SpotifyLikeAppExampleCode {

  // the current audio clip
  private static Clip audioClip;

  // scanner for user input
  private static Scanner input = new Scanner(System.in);

  private static String directoryPath = ".";

  // "main" makes this class a java app that can be executed
  public static void main(final String[] args) {

    // reading audio library from json file
    Song[] library = readAudioLibrary();

    String userInput = "";

    while (!userInput.equals("q")) {

      menu();

      // get input
      userInput = input.nextLine();

      // accept upper or lower case commands
      userInput = userInput.toLowerCase();

      // do something
      handleMenu(userInput, library);
    }

    // close the scanner
    input.close();
  }

  /*
   * displays the menu for the app
   */
  public static void menu() {

    System.out.println("");
    System.out.println("---- SpotifyLikeApp ----");
    System.out.println("[H]ome");
    System.out.println("[S]earch by title");
    System.out.println("[L]ibrary");
    System.out.println("[F]avorites");
    System.out.println("[Q]uit");

    System.out.println("");
    System.out.print("Enter an option: ");
  }

  /*
   * handles the user input for the app
   */
  public static void handleMenu(String userInput, Song[] library) {

    switch (userInput) {

      case "h":
        System.out.println("-->Home<--");
        System.out.println("Welcome to the Spotify-like Music App");
        System.out.println("Songs in library: " + library.length);
        System.out.println("Use Search to find a song by title.");
        System.out.println("Use Library to browse and play songs.");
        break;

      case "s":
        System.out.println("-->Search by title<--");
        searchByTitle(library);
        break;

      case "l":
        System.out.println("-->Library<--");
        showLibrary(library);
        break;

      case "f":
        System.out.println("-->Favorites<--");
        showFavorites(library);
        break;

      case "q":
        System.out.println("-->Quit<--");
        break;

      default:
        System.out.println("Invalid option. Please choose H, S, L, F, or Q.");
        break;
    }
  }

  /*
   * displays all songs in the library
   */
  public static void showLibrary(Song[] library) {

    for (int i = 0; i < library.length; i++) {

      System.out.println((i + 1) + ". " +
          library[i].title() + " - " +
          library[i].artist());
    }

    System.out.print("Enter a song number to play: ");
    int choice = Integer.parseInt(input.nextLine());

    play(library, choice - 1);
  }

  /*
   * displays favorite songs
   */
  public static void showFavorites(Song[] library) {

    boolean foundFavorite = false;

    for (int i = 0; i < library.length; i++) {

      if (library[i].isFavorite()) {

        System.out.println((i + 1) + ". " +
            library[i].title() + " - " +
            library[i].artist());

        foundFavorite = true;
      }
    }

    if (!foundFavorite) {
      System.out.println("You do not have any favorite songs yet.");
      return;
    }

    System.out.print("Enter a favorite song number to play: ");
    int choice = Integer.parseInt(input.nextLine());

    play(library, choice - 1);
  }

  /*
   * searches for a song by title
   */
  public static void searchByTitle(Song[] library) {

    System.out.print("Enter a song title: ");
    String search = input.nextLine().toLowerCase();

    boolean foundSong = false;

    for (int i = 0; i < library.length; i++) {

      if (library[i].title().toLowerCase().contains(search)) {

        System.out.println((i + 1) + ". " +
            library[i].title() + " - " +
            library[i].artist());

        foundSong = true;

        play(library, i);
      }
    }

    if (!foundSong) {
      System.out.println("Song not found.");
    }
  }

  /*
   * plays an audio file
   */
  public static void play(Song[] library, int songIndex) {

    // get the filePath and open an audio file
    final String filename = library[songIndex].filePath();
    final String filePath = directoryPath + "/wav/" + filename;
    final File file = new File(filePath);

    // stop the current song from playing before playing the next one
    if (audioClip != null) {
      audioClip.close();
    }

    // display song information
    System.out.println("");
    System.out.println("Title: " + library[songIndex].title());
    System.out.println("Artist: " + library[songIndex].artist());
    System.out.println("Year: " + library[songIndex].year());
    System.out.println("Genre: " + library[songIndex].genre());
    System.out.println("Favorite: " + library[songIndex].isFavorite());
    System.out.println("File: " + library[songIndex].filePath());

    try {

      // create clip
      audioClip = AudioSystem.getClip();

      // get input stream
      final AudioInputStream in =
          AudioSystem.getAudioInputStream(file);

      audioClip.open(in);
      audioClip.setMicrosecondPosition(0);
      audioClip.start();

      // open song controls
      songControls(library, songIndex);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /*
   * controls the currently playing song
   */
  public static void songControls(Song[] library, int songIndex) {

    boolean controlsOpen = true;
    boolean paused = false;
    boolean stopped = false;

    while (controlsOpen) {

      System.out.println("");
      System.out.println("---- Song Controls ----");
      System.out.println("[P]ause/Resume");
      System.out.println("[S]top");
      System.out.println("[R]ewind 5 seconds");
      System.out.println("[F]orward 5 seconds");
      System.out.println("[V]Favorite");
      System.out.println("[B]ack");
      System.out.print("Enter an option: ");

      String choice = input.nextLine().toLowerCase();

      switch (choice) {

        case "p":

          if (paused || stopped) {

            audioClip.start();
            paused = false;
            stopped = false;

          } else {

            audioClip.stop();
            paused = true;
          }

          break;

        case "s":

          audioClip.stop();
          audioClip.setMicrosecondPosition(0);

          paused = false;
          stopped = true;

          System.out.println("Song stopped.");
          break;

        case "r":

          long rewind =
              audioClip.getMicrosecondPosition() - 5000000;

          if (rewind < 0) {
            rewind = 0;
          }

          audioClip.setMicrosecondPosition(rewind);

          System.out.println("Rewound 5 seconds.");
          break;

        case "f":

          long forward =
              audioClip.getMicrosecondPosition() + 5000000;

          if (forward > audioClip.getMicrosecondLength()) {
            forward = audioClip.getMicrosecondLength();
          }

          audioClip.setMicrosecondPosition(forward);

          System.out.println("Forwarded 5 seconds.");
          break;

        case "v":

          library[songIndex].toggleFavorite();

          System.out.println(
              "Favorite: " + library[songIndex].isFavorite());

          break;

        case "b":

          controlsOpen = false;
          break;

        default:

          System.out.println("Invalid option.");
          break;
      }
    }
  }

  /*
   * read the audio library of music
   */
  public static Song[] readAudioLibrary() {

    // get the file path
    final String jsonFileName = "audio-library.json";
    final String filePath = directoryPath + "/" + jsonFileName;

    Song[] library = null;

    try {

      System.out.println("Reading the file " + filePath);

      JsonReader reader =
          new JsonReader(new FileReader(filePath));

      library = new Gson().fromJson(reader, Song[].class);

    } catch (Exception e) {

      System.out.printf(
          "ERROR: unable to read the file %s\n", filePath);

      System.out.println();
    }

    return library;
  }
}
