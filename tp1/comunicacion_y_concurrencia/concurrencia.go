package main

import (
	"errors"
	"fmt"
	"os"
	"strings"
	"sync"
)

// struct containing the data to obtain
type information struct {
	notEmptyLines      int
	words              int
	characters         int
	spaces             int
	frequencies        map[string]int
	mostFrequentWord   string
	mostFrequentAmount int
}

// string method to print information on screen
func (i information) String() string {
	return fmt.Sprintf("Not empty lines: %d\nWord amount: %d\nCharacter amount: %d\nSpaces amount: %d\nMost frequent word: \"%s\" with count = %d", i.notEmptyLines,
		i.words, i.characters, i.spaces, i.mostFrequentWord, i.mostFrequentAmount)
}

// global variables
var (
	info  information // the structure in which the threads will write their information
	mutex sync.Mutex  // semaphore to synchronize access to the info global variable
)

func main() {
	if len(os.Args) != 2 {
		fmt.Println("FilePath argument is required")
		return
	}

	filePath := os.Args[1]

	bytes, err := os.ReadFile(filePath)
	if err != nil {
		if errors.Is(err, os.ErrNotExist) {
			fmt.Println("The file ", filePath, " does not exist")
			return
		}

		panic(err)
	}

	text := string(bytes)
	info.frequencies = make(map[string]int)
	var wg sync.WaitGroup

	lines := strings.Split(text, "\n")

	for _, line := range lines {
		line = strings.TrimSpace(line) //remove \r at the end of the line
		if line != "" {
			wg.Add(1)
			go processLine(line, &wg)
		}
	}

	wg.Wait()

	info.mostFrequentWord, info.mostFrequentAmount = mostFrequent()

	fmt.Println(info)
}

func processLine(line string, wg *sync.WaitGroup) {
	words := countWords(line)
	chars := countChars(line)
	spaces := countSpaces(line)
	freq := calculateFrequency(line)
	mutex.Lock()
	info.notEmptyLines++
	info.words += words
	info.characters += chars
	info.spaces += spaces
	for word, count := range freq {
		info.frequencies[word] += count
	}
	mutex.Unlock()
	wg.Done()
}

func countWords(line string) int {
	return len(strings.Split(line, " "))
}

func countChars(line string) int {
	return len(line)
}

func countSpaces(line string) int {
	return strings.Count(line, " ")
}

func calculateFrequency(line string) map[string]int {
	wordFreq := make(map[string]int)
	for _, word := range strings.Split(line, " ") {
		wordFreq[word]++
	}
	return wordFreq
}

func mostFrequent() (mostFreqWord string, max int) {
	max = 0
	mostFreqWord = ""
	for word, count := range info.frequencies {
		if count > max {
			max = count
			mostFreqWord = word
		}
	}
	return
}

