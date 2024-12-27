package application;

import javafx.scene.control.Alert;
import java.util.List;
import java.util.Random;

public class ComfortingMessageHandler {
	
    private static final List<String> comfortingMessages = List.of(
            "It's okay to feel down sometimes. \nRemember, you're not alone.",
            "Take a deep breath and focus on the positive. \nYou've got this!",
            "Remember, tough times don't last, but tough people do.",
            "It's okay to take a break. \nYou deserve peace and rest.",
            "You are strong and capable of overcoming anything.",
            "Don't forget that you're loved and appreciated by those around you.",
            "Sometimes life can be overwhelming, but you're resilient.",
            "You're doing better than you think. \nTake it one step at a time.",
            "It's okay to ask for help. \nYou don't have to go through this alone.",
            "Every difficult moment is an opportunity for growth. \nKeep going.",
            "You're stronger than the challenges you're facing. You've got this!",
            "Take one moment at a time. \nThere's no rush to feel better immediately.",
            "It's okay to have bad days. \nThey don’t define you.",
            "You've already come so far. \nKeep pushing forward.",
            "Sometimes, the best thing you can do is rest and recharge.",
            "Remember to be kind to yourself, especially during tough times.",
            "You are worthy of peace, love, and happiness. \nNever forget that.",
            "Things may feel hard now, but this is just a chapter, not your whole story.",
            "Take a moment to appreciate how far you've come. \nYou are making progress.",
            "Every day is a new opportunity to heal and move forward.",
            "Allow yourself grace. \nIt’s okay to not be okay all the time.",
            "Remember, you are not defined by your struggles. \nYou are much more than that.",
            "Even in dark times, remember that light will come again. \nStay hopeful.",
            "You're not alone. \nReach out if you need someone to talk to.",
            "You have the strength to get through this, even when it feels tough.",
            "You are resilient and capable of handling whatever comes your way.",
            "Sometimes, it's okay to just take a step back and breathe. \nYou're doing great.",
            "You’ve already overcome challenges before, \nand you’ll do it again.",
            "Don't be afraid to take things one day at a time.",
            "Remember that healing is a journey. \nBe gentle with yourself along the way.",
            "Even when it feels like no one understands, there are people who care.",
            "Every day is a fresh start. \nYou have the power to make today better.",
            "Be proud of yourself for getting through today. \nEvery step matters.",
            "You are more than your emotions. \nYou are loved, valued, and important."
    );

    public static void showComfortingMessage() {
        String comfortingMessage = comfortingMessages.get(new Random().nextInt(comfortingMessages.size()));

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notice!");
        alert.setHeaderText("Hi! We noticed that you've been feeling low these days. ");
        alert.setContentText("\n" +comfortingMessage);
        alert.showAndWait();
    }
}


