#ifndef ADVANCE_STACK_H
#define ADVANCE_STACK_H

#include <climits>   // For initialising min and max stacks.
#include <iostream>  // Used only for print().
#include "Stack.h"
#include "StackUnderflowException.h"
#include "UnbalancedConcatenationException.h"

// This is my implementation of a stack using a doubly linked list.

// Yes, the min/max functions do assume you're storing an integer despite this
// being a template class, and also it should be called AdvancedStack,
// but this was the file I was told to turn in.

/*
 * =====NODE======================================
 *  [prev: Node<T>*]<--[value: T]-->[next: Node<T>*]
 * ============================ (nullptr at init)
 */
template <class T>
struct Node  // Init: {prev, value}
{
    Node<T>* prev;
    const T value;
    Node<T>* next = nullptr;
};

/*
 * =====ADVANCE_STACK=======================================
 *  Main: (0)<->[25]<->[10]<->[30]<->[5]<->[50]-->(nullptr)
 *        *BASE               *middle      *head
 *   Min: (0)<->[25]<->[10]<->[5]
 *                            *min
 *   Max: (0)<->[25]<->[30]<->[50]
 * ========================== *max =========================
 */
template <class T>
class AdvanceStack : public Stack<T>
{
   private:
    int size;         // Number of elements in main stack, not including BASE node.
    Node<T>* BASE;    // Dummy node used when main stack is empty.
    Node<T>* head;    // Top element of main stack.
    Node<T>* middle;  // Element (size/2 rounded up) of main stack.
    Node<T>* min;     // Top element of minimum value stack.
    Node<T>* max;     // Top element of maximum value stack.
    void deleteStack(Node<T>*);
    void updateMinMax(const T&);

   public:
    // Constructor & Destructor:
    AdvanceStack();
    ~AdvanceStack();
    // Size Checks:
    bool isEmpty() const { return not size; }
    bool isFull() const { return false; }
    // Mutators:
    void push(const T&);
    void pop();
    void deleteMiddle();
    void concatenate(const AdvanceStack<T>&);
    // Getters:
    T peek() const;
    T getMin() const;
    T getMax() const;
    T getMiddle() const;
    void print() const;
};


// CONSTRUCTOR & DESTRUCTOR //
template <class T>
AdvanceStack<T>::AdvanceStack() :
    size(0),
    // Initialise zeroth element of main stack with dummy value of 0:
    BASE(new Node<T>{nullptr, 0}),
    head(BASE),
    middle(BASE),
    // INT_MAX/MIN ensure first item pushed always passes both min/max checks:
    min(new Node<T>{nullptr, INT_MAX}),
    max(new Node<T>{nullptr, INT_MIN}) {}

template <class T>
AdvanceStack<T>::~AdvanceStack() {
    deleteStack(min);
    deleteStack(max);
    deleteStack(head);
}

template <class T>
void AdvanceStack<T>::deleteStack(Node<T>* curr) {
    while (curr) {
        Node<T>* tempNode = curr;
        curr = curr->prev;
        delete tempNode;
    }
}


// MUTATORS //
template <class T>
void AdvanceStack<T>::push(const T& value) {
    head->next = new Node<T>{head, value};
    head = head->next;
    size++;

    if (size % 2 == 1) {  // Is odd size.
        middle = middle->next;
    }
    if (value < min->value) {
        min->next = new Node<T>{min, value};  // New min is value.
        min = min->next;
    }
    if (value > max->value) {
        max->next = new Node<T>{max, value};  // New max is value.
        max = max->next;
    }
}

template <class T>
void AdvanceStack<T>::pop() {
    if (isEmpty()) {
        throw StackUnderflowException();
    }
    if (size % 2 == 0) {  // Is even size.
        middle = middle->prev;
    }
    updateMinMax(peek());

    head = head->prev;
    delete head->next;
    head->next = nullptr;
    size--;
}

template <class T>
void AdvanceStack<T>::deleteMiddle() {
    if (isEmpty()) {
        throw StackUnderflowException();
    }
    Node<T>* oldMiddle = middle;

    if (size % 2 == 0) {  // Is even size.
        middle = middle->next;
        middle->prev = oldMiddle->prev;
        middle->prev->next = middle;
    }
    else {  // Is odd size.
        middle = middle->prev;
        middle->next = oldMiddle->next;
        if (middle->next) {
            middle->next->prev = middle;
        }
    }
    updateMinMax(oldMiddle->value);

    delete oldMiddle;
    size--;

    if (isEmpty()) {
        head = BASE;
    }
}

template <class T>
void AdvanceStack<T>::updateMinMax(const T& value) {
    if (value == getMin()) {
        min = min->prev;
        delete min->next;
        min->next = nullptr;
    }
    if (value == getMax()) {
        max = max->prev;
        delete max->next;
        max->next = nullptr;
    }
}

template <class T>
void AdvanceStack<T>::concatenate(const AdvanceStack<T>& otherStack) {
    Node<T>* curr = otherStack.BASE->next;
    while (curr) {
        push(curr->value);
        curr = curr->next;
    }
}


// GETTERS //
template <class T>
T AdvanceStack<T>::peek() const {
    if (isEmpty()) {
        throw StackUnderflowException();
    }
    return head->value;
}

template <class T>
T AdvanceStack<T>::getMin() const {
    if (isEmpty()) {
        throw StackUnderflowException();
    }
    return min->value;
}

template <class T>
T AdvanceStack<T>::getMax() const {
    if (isEmpty()) {
        throw StackUnderflowException();
    }
    return max->value;
}

template <class T>
T AdvanceStack<T>::getMiddle() const {
    if (isEmpty()) {
        throw StackUnderflowException();
    }
    return middle->value;
}

template <class T>
void AdvanceStack<T>::print() const {
    if (isEmpty()) {
        throw StackUnderflowException();
    }
    std::cout << "[" << size << "]: (";

    Node<T>* curr = BASE->next;  // 1st Element.
    while (curr) {
        std::cout << curr->value << (curr->next ? ", " : "");
        curr = curr->next;
    }
    std::cout << ")\n";
}

#endif  // ADVANCE_STACK_H
