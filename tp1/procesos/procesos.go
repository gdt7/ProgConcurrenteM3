package main

import (
	"fmt"
	"os"
	"os/exec"
	"strconv"
	"time"
)

const (
	timeToSleep   time.Duration = 100   //amount of seconds to wait for each process to finish, in order to check the process tree
	procArgLen    int           = 2     //the arguments should be 2 for a valid process in this case (filePath and letter)
	filePathIdx   int           = 0     //argument 0: file's path
	letterArgIdx  int           = 1     //argument 1: letter corresponding to that process
	checkPstree   bool          = true //boolean that defines if each process has to wait in order to get the pstree
	defaultLetter string        = "A"
	processLetter1, processLetter2, processLetter3, processLetter4,
	processLetter5, processLetter6, processLetter7, processLetter8,
	processLetter9, processLetter10 string = "A", "B", "C", "D", "E", "F", "G", "H", "I", "J" //letters corresponding to each process in the tree
)

func main() {
	start := time.Now()
	curLetter := attendCurrentProcess()
	followingLetters := followingLetters(curLetter)
	childrenProcesses, err := createChildrenProcesses(followingLetters)
	if err != nil {
		fmt.Println(err)
		return
	}

	//if this variable is true, perform an active wait in the process to be able to see the tree.
	if checkPstree {
		time.Sleep(timeToSleep * time.Second)
	}
	if err = waitForChildrenProcesses(childrenProcesses); err != nil {
		fmt.Println(err)
		return
	}

	//track the execution time in case it's the main process.
	if curLetter == defaultLetter {
		timeTrack(start, curLetter)
	}
}

func attendCurrentProcess() (curLetter string) {
	pid := strconv.Itoa(os.Getpid())
	//main process:
	if len(os.Args) < procArgLen {
		curLetter = defaultLetter
		fmt.Println("Soy el proceso de letra:", curLetter, ", Mi PID es:", pid)
	} else { //children process
		curLetter = os.Args[letterArgIdx]
		fatherPid := os.Getppid()
		fmt.Println("Soy el proceso de letra:", curLetter, ", Mi PID es:", pid, ", El PID de mi padre es:", fatherPid)
	}
	return
}

func followingLetters(letter string) (followingLetters []string) {
	switch letter {
	case defaultLetter:
		followingLetters = []string{processLetter2, processLetter3, processLetter4}
	case processLetter2:
		followingLetters = []string{processLetter5, processLetter6}
	case processLetter4:
		followingLetters = []string{processLetter7}
	case processLetter6:
		followingLetters = []string{processLetter8, processLetter9}
	case processLetter9:
		followingLetters = []string{processLetter10}
	}
	return
}

func createChildrenProcesses(followingLetters []string) ([]*exec.Cmd, error) {
	//os.Args[filePathIdx] is this file's path, to execute main once again.
	filePath := os.Args[filePathIdx]

	var childrenProcesses []*exec.Cmd
	for _, letter := range followingLetters {
		if err := initChildProcess(&childrenProcesses, filePath, letter); err != nil {
			return nil, err
		}
	}
	return childrenProcesses, nil
}

func initChildProcess(childrenProcesses *[]*exec.Cmd, filePath string, args ...string) (err error) {
	//arguments: the letter, current PID (it's child's parent).
	cmd := exec.Command(filePath, args...)
	*childrenProcesses = append(*childrenProcesses, cmd)
	// Set the child process's output to the standard output of the parent process
	cmd.Stdout = os.Stdout
	if err = cmd.Start(); err != nil {
		return fmt.Errorf("Error starting child process: %w", err)
	}
	return
}

func waitForChildrenProcesses(childrenProcesses []*exec.Cmd) error {
	for _, proc := range childrenProcesses {
		if err := proc.Wait(); err != nil {
			return fmt.Errorf("Error waiting for child process: %w", err)
		}
	}
	return nil
}

func timeTrack(start time.Time, name string) {
	elapsed := time.Since(start)
	fmt.Printf("\n%s ejecutó en %s\n", name, elapsed)
}
