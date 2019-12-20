package org.uppaal.cli;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;

/**
 * Unit test for the command class
 * @author Marius Mikucionis <marius@cs.aau.dk>
 */
public class CommandTest
{
    
    public CommandTest()
    {
    }
    
    @BeforeAll
    public static void setUpClass()
    {
    }
    
    @AfterAll
    public static void tearDownClass()
    {
    }
    
    @BeforeEach
    public void setUp()
    {
    }
    
    @AfterEach
    public void tearDown()
    {
    }

    /**
     * Test of getCommand method, of class Command.
     */
    @Test
    @DisplayName("parse command")    
    public void testGetCommand()
    {
        System.out.println("getCommand");
        Command cmd = new Command("help");
        assertEquals("help", cmd.getCommand());
        cmd = new Command(" \t help");
        assertEquals("help", cmd.getCommand());
        cmd = new Command("help \t  ");
        assertEquals("help", cmd.getCommand());
        cmd = new Command("help \t  help");
        assertEquals("help", cmd.getCommand());
    }

    /**
     * Test of getArgs method, of class Command.
     */
    @Test
    @DisplayName("parse arguments")
    public void testGetArgs()
    {
        System.out.println("getArgs");
        Command cmd = new Command("help");
        assertEquals("", cmd.getArgs());
        cmd = new Command(" \t help");
        assertEquals("", cmd.getArgs());
        cmd = new Command("help \t  ");
        assertEquals("", cmd.getArgs());
        cmd = new Command("help \t  me");
        assertEquals("me", cmd.getArgs());
        cmd = new Command("help \t  me now!");
        assertEquals("me now!", cmd.getArgs());
    }
    
}
