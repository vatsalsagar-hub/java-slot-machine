# Java Slot Machine

A rebuilt and expanded version of an earlier Java course project. This portfolio version simulates a five-reel slot machine with account persistence, randomized spins, balance management, payout logic, session statistics, and spin history.

## Features

- 5 randomized reels
- 7 possible symbols
- Username-based account loading
- $10 starting balance for new accounts
- $1–$3 bet validation
- File-based balance persistence
- Visible payout rules
- Session stats: spins, wins, losses, win rate, and biggest win
- Spin-history viewer for the latest 10 spins
- Swing `JOptionPane` interface

## Payout Rules

| Match | Payout |
| --- | ---: |
| 5 matching symbols | 10× bet |
| 4 matching symbols | 5× bet |
| 3 matching symbols | 3× bet |
| 2 matching symbols | 2× bet |
| No match | $0 |

## Concepts Demonstrated

- Object-oriented program organization
- Arrays and nested loops
- Random number generation
- Input validation
- File I/O
- State management
- Basic statistics
- Swing dialog-based user interface

## Run It

```bash
javac src/FinalProject.java
java -cp src FinalProject
```

The application stores account balances in `slot_machine_account.txt` in the working directory.

## Repository Structure

```text
java-slot-machine/
├── README.md
└── src/
    └── FinalProject.java
```

## Portfolio Note

This repository is a rebuilt version of an earlier Java slot-machine course project whose original source file was no longer available. The portfolio version was reconstructed from preserved project requirements and then extended with stats, spin history, and clearer payout rules.

**Author:** Vatsal Sagar
