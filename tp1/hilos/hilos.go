package main

import (
	"fmt"
	"math/rand"
	"os"
	"reflect"
	"strconv"
	"sync"
	"time"
)

var (
	initialMatrix    [][]int
	sequentialMatrix [][]int
	concurrentMatrix [][]int
)

const (
	showMatrices    = false
	maxThreads      = 300
	maxSize         = 10000
	maxVisibleSize  = 50
	maxRandomNumber = 10
	dimensionPos    = 1
	threadsPos      = 2
)

func main() {
	//testing the program with arguments.
	if len(os.Args) == 3 {
		sizeToTest, err := strconv.Atoi(os.Args[dimensionPos])
		if err != nil {
			panic(err.Error())
		}

		threadsToTest, err := strconv.Atoi(os.Args[threadsPos])
		if err != nil {
			panic(err.Error())
		}

		scalarToTest := 10

		prevalidateArguments(&sizeToTest, &threadsToTest)
		initMatrices(sizeToTest)

		sequentialStart := time.Now()
		multiplyMatrixSequential(scalarToTest)
		sequentialEnd := time.Since(sequentialStart)

		concurrentStart := time.Now()
		multiplyMatrixConcurrent(scalarToTest, threadsToTest)
		concurrentEnd := time.Since(concurrentStart)

		equal := compareMatrices(sequentialMatrix, concurrentMatrix)
		fmt.Println("The matrices are equal: ", equal)

		if showMatrices && sizeToTest < maxVisibleSize {
			printMatrix(initialMatrix)
			printMatrix(sequentialMatrix)
			printMatrix(concurrentMatrix)
		}

		fmt.Println("Time elapsed for sequential: ", sequentialEnd.Milliseconds(), "ms")
		fmt.Println("Time elapsed for concurrent: ", concurrentEnd.Milliseconds(), "ms")

	} else { //testing the program with the matrix
		threads := []int{0, 1, 2, 4}
		sizes := []int{10, 20, 40, 80}
		scalar := 10
		results := measureTimes(threads, sizes, scalar)
		printResults(results, threads, sizes)
	}
}

func printResults(results [][]time.Duration, threads []int, sizes []int) {
	for idxSize, row := range results {
		fmt.Print("N:", sizes[idxSize], "\n\t")
		for idxThread := range row {
			if idxThread == 0 {
				fmt.Print("Secuencial:", results[idxSize][idxThread], "\t")
			} else {
				fmt.Print(threads[idxThread], " Hilos:", results[idxSize][idxThread], "\t")
			}
		}
		fmt.Println()
	}
}

func measureTimes(threads []int, sizes []int, scalar int) (results [][]time.Duration) {
	results = make([][]time.Duration, len(sizes), len(threads))
	for idxSize, size := range sizes {
		initMatrices(size)
		for _, thread := range threads {
			times := measureTime(size, thread, scalar)
			results[idxSize] = append(results[idxSize], times)
		}
	}
	return
}

func measureTime(size int, threads int, scalar int) time.Duration {
	prevalidateArguments(&size, &threads)
	start := time.Now()
	if threads == 0 {
		multiplyMatrixSequential(scalar)
	} else {
		multiplyMatrixConcurrent(scalar, threads)
	}

	return time.Since(start)
}

func prevalidateArguments(size *int, threads *int) {
	if *size > maxSize {
		*size = maxSize
	}
	//if there are more threads than rows, set the amount of threads to the amount of rows.
	if *threads > *size {
		*threads = *size
	}
	//if the amount of threads is still larger than the max amount, set the amount of threads to the max amount.
	if *threads > maxThreads {
		*threads = maxThreads
	}
}

func initMatrices(size int) {
	initialMatrix = initMatrixRandom(size)
	sequentialMatrix = initMatrixZeroes(size)
	concurrentMatrix = initMatrixZeroes(size)
}

func initMatrixRandom(size int) [][]int {
	mat := make([][]int, size)
	for idxRow := range mat {
		for i := 0; i < size; i++ {
			mat[idxRow] = append(mat[idxRow], rand.Intn(maxRandomNumber))
		}
	}
	return mat
}

func initMatrixZeroes(size int) [][]int {
	mat := make([][]int, size)
	for idxRow := range mat {
		for i := 0; i < size; i++ {
			mat[idxRow] = append(mat[idxRow], 0)
		}
	}
	return mat
}

func multiplyMatrixSequential(scalar int) {
	for idxRow, row := range initialMatrix {
		for idxCol, val := range row {
			sequentialMatrix[idxRow][idxCol] = scalar * val
		}
	}
}

func multiplyMatrixConcurrent(scalar int, threads int) {
	rowsPerThread := calculateRowsPerThread(len(initialMatrix), threads)
	var wg sync.WaitGroup
	initialRow := 0
	for t := 0; t < threads; t++ {
		wg.Add(1)
		go multiplyRowsConcurrent(scalar, initialRow, initialRow+rowsPerThread[t], &wg)
		initialRow += rowsPerThread[t]
	}

	defer wg.Wait()
}

func calculateRowsPerThread(rows int, threads int) []int {
	rpt := make([]int, threads)
	for i := 0; i < threads; i++ {
		size := int(rows / threads)
		if i < rows%threads {
			size++
		}
		rpt[i] = size
	}
	return rpt
}

func multiplyRowsConcurrent(scalar int, initialRow int, finalRow int, wg *sync.WaitGroup) {
	defer wg.Done()
	n := len(initialMatrix)
	for i := initialRow; i < finalRow; i++ {
		for j := 0; j < n; j++ {
			concurrentMatrix[i][j] = initialMatrix[i][j] * scalar
		}
	}
}

func compareMatrices(mat1 [][]int, mat2 [][]int) bool {
	return reflect.DeepEqual(mat1, mat2)

}

func printMatrix(mat [][]int) {
	for _, row := range mat {
		fmt.Println(row)
	}
	fmt.Println("")
}
