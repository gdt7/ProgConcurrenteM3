import os
import time
import threading

#GLOBAL VARIABLES
SLEEP_TIME = 100
CHECK_PSTREE = False
IS_CHILD = 0

def childrenCreatorHandler(option):
    match option:
        case 'B':
            createChild("EF")

        case 'D':
            createChild("G")

        case 'F':
            createChild("HI")

        case 'I':
            createChild("J")

def createChild(children):
    global CHECK_PSTREE
    children_number = len(children)
    for child in children:
        if os.fork() == IS_CHILD:
            print("[", child, "] -> PID: ", os.getpid(), "PPID: ", os.getppid())
            childrenCreatorHandler(child)
            if CHECK_PSTREE:
              time.sleep(SLEEP_TIME)
            os._exit(os.EX_OK)

    for i in range(0,children_number):
        os.wait()


def main():
    print("[ A ] -> PID: ", os.getpid())
    createChild("BCD")

startTimer = time.time()
main()
endTimer = time.time()
print("Duracion total: ",endTimer-startTimer, "s")
