# primetimes
Hmm. A coding challenge involves determining the first N primes, without using a certain library that would help with that. Fine, I am at heart a Common Lisper, I love ignoring Java*, the Gosling/Steele un-contrib that had The Great Unwashed driving with the brakes on until Rich Hickey begat The Second Coming of John Mccarthy's Lisp.

* Kinda. I used it to test.

## Stage 1: Planning
We think through the technology, where to improve the requirements, and where to go all Dukes of Hazzard on management roadblocks.

### Cheating on prime numbers

Searching for "clojure prime numbers" I quickly find [The Forbidden Fruit](https://github.com/hypirion/primes). Clojure is good that way, libs for everything. I add `[com.hypirion/primes "0.2.2"]` to my project dependencies.

But I like to keep an open mind -- maybe this prospective employer knows what they are doing -- and grab also the prime number code from some [cattle rustler](http://www.thesoftwaresimpleton.com/blog/2015/02/07/primes/). Ha-ha, challenge! You tried to make me write a prime number generator, but there is more than one way to skin a code theft! 

Paul the rustler done good, beats hypirion by about an order of magnitude to the millionth prime. But then I notice the fine print. Not only must I eschew The Forbidden Fruit, I must *(write your own code)*. My prospective employer is losing points fast. Must I also leave out anything I have learned about programming before doing the challenge? If I took an algorithm's class must I avoid any techniques learned? [Alpha Zero](https://youtu.be/NxtEDLpJoqQ), come here! I need you!

Mind you, I have them on a technicality: if I were writing my own code I would go on Google, find Paul's hack and go with it. But along the way I was surprised by [this insight](https://primes.utm.edu/notes/faq/six.html), so I will likely come back to this to see how well I can do with that. Sadly, I do not see how to leverage the divide by six insight in combination with the sieve, so in the end I will prolly code my own to make the boss happy.

For now I will use Rustler's Primes and get on with the crux of the project: the table formatting.
>
> The challenge, good for it, has flushed me out a second time, revealing
> my policy of starting with the crux. We manage risk by failing fast.
> 

### Cheating on the crux
The challenge is, given N, print out the moral equivalent of the following for `n = 5`:
````
    2  3  5
2   4  6 10
3   6  9 15
5  10 15 25
````
Again I will challenge the challenger and correct the spec to incude some ASCII adornment:
````
  |  2  3  5 |
--|----------|
2 |  4  6 10 |
3 |  6  9 15 |
5 | 10 15 25 |
--|----------|
````
I wonder if they will be concerned that I cheated on formatting by calling on my Common Lisp training and using [`clojure.pretty-print/cl-format`](https://clojuredocs.org/clojure.pprint/cl-format), a nifty Clojure implementation of Common Lisp [format](http://www.lispworks.com/documentation/lw50/CLHS/Body/f_format.htm). 

From memory:

````lisp
(pp/cl-format s "~vd | ~{~vd~}" row-no-width row-no product-width products)
````
I just have to remind myself of how to right-justify, check my use of ~vm, and check my width qualifier syntax -- the rule is, if you can code Lisp format from memory you need to do something else for a while.

>
> Wait! Format is slow precisely because it *is* a DSL! Which must be interpreted! But (a) we are
> printing to the console which is a pig anyway and (b) I am not exactly getting paid to do this
> so we will just note the concern.
>

### Insubordination on testing
The spec requires TDD. There again the challenge will be challenged. Do I need tests *during development*? Probably not, though I can write some up *after* development to make refactoring safer. We will see, but the instructions might have been "Use TDD where and how you deem best, explaining your choices."

### Compliance. Finally.
The instructions were accompanied by this injunction: "[include] a bit of a narrative of why you did what you did.
". Hence this write-up. And narrative it will be: the above will be retained mostly as is, with new narrative as we work.

## First Contact
Common Lisp format is a hoot and always a challenge, but after a brief battle it succumbed to my twenty plus years of experience:
````clojure
(defn row-fmt [cell-width row-n row]
  (pp/cl-format nil
    "~v@a | ~{~vd~} |" cell-width row-n
    (interleave
      (repeat cell-width)
      row)))
````
And now life is good. Displaying just a couple of rows to confirm:
````clojure
(doseq [p [3 5]]
  (println (row-fmt 4 p [2 3 5 7 11 13])))

=>
   3 |    2   3   5   7  11  13 |
   5 |    2   3   5   7  11  13 |
````
 Fine. Now wrapped with some more code to handle the multiplication and some nice ASCII tabling:
````bash
(println-prime-times 5 2)

=>
------|---------------------------|
    X |     2    3    5    7   11 |
------|---------------------------|
    2 |     4    6   10   14   22 |
    3 |     6    9   15   21   33 |
    5 |    10   15   25   35   55 |
    7 |    14   21   35   49   77 |
   11 |    22   33   55   77  121 |
------|---------------------------|
````
But along the way I had to solve some easy-to-reintroduce bugs that messed up the layout, so we will toss in a test at the end to support refactoring*.
>
> Inclination #3: Defer drudgery. Keep the fun going. Tedious stuff is more bearable when the prize is in hand.
>
> * Nah, testing formatted report layout for things like alignment bugs would be a bear and
> and I am *still* not getting paid for this so again we will just note the concern.
>

Now it is time to build a standalone binary accepting command-line parameters, using my old stand-bys clojure [tools.cli](https://github.com/clojure/tools.cli) and [lein bin-plus](https://github.com/BrunoBonacci/lein-binplus).
## Prime numbers stand alone
Voila! No commentary. Both tools are well documented and a joy to use.

![Times table for First 15 Primes](https://github.com/kennytilton/primetimes/blob/master/doc/table-15.jpg)

## Stage 3: Make the boss happy
Now just for the fun of it let us write our own prime number generator and make the boss happy.

### My very own prime generator
Ignoring Mr. Cowan's work is hard once seen, so we will fake reproducing his work from scratch.

Contemplation of the Sieve of Eratosthenes makes clear we need to know how far to go when propagating out the multiples of identified primes. Mr. Cowan recommends Wikipedia, but my best find was [on Stack Overflow](https://stackoverflow.com/questions/9625663/calculating-and-printing-the-nth-prime-number):
````
n*(log n + log (log n) - 1) < p(n) < n*(log n + log (log n)), for n >= 6.
````
The nth prime `p(n)` will fall within those bounds. Meanwhile, I was wondering why Paul threw in an airbag `...+ 3` in his implementation thereof. That was a clever, un-self-documenting way of avoiding `for n >= 6`! Let us be more transparent:
````clojure
(defn nth-prime-upper-bound
  "We figure out how Where p(n) is the nth prime:

      n*(log n + log (log n) - 1) < p(n) < n*(log n + log (log n)), for n >= 6"
  [n]
  (cond
    ;; ie, 2 3 5, with a bit of waste for n < 5
    (< n 6) 13
    ;; now the formula
    :default (let [logn (Math/log n)]
               (Math/ceil
                 (* n (+ logn (Math/log logn)))))))
````
Now we can build a sieve, working from (stealing) Paul's code but improving the data names and adding an efficiency that gets increasingly helpful at scale:
````
(defn sieve
  "Build a boolean array big enough to hold the
  nth prime where only values at prime indices are true."

  [prime-upper-bound]

  (let [max-factor (Math/sqrt prime-upper-bound)
        sieve (boolean-array prime-upper-bound true)]
    (loop [p 2]
      (when (<= p max-factor)
        (when (aget sieve p)
          ;; we have a prime; propagate out via fast addition.
          ;; we can start at the square because values between
          ;; (+ x x), the apparent next, and (* x x) will have been handled by
          ;; propagation of earlier primes.
          ;;
          ;; eg, when we find 5 is prime, 10, 15, and 20 will have been cleared
          ;; by 2, 3, and 4.
          ;;
          (loop [p-product (* p p)]
            (when (< p-product prime-upper-bound)                                   ;; do not pass end of array
              (aset sieve p-product false)
              (recur (+ p-product p)))))                            ;; look at next multiple of x
        (recur (inc p))))
    sieve))
````
The efficiency? Propagating from x^2 instead of 2x. See the comment for why this works. The rest of the code is Paul's:
````
(defn primes [n]
  (let [max-factor (Math/sqrt n)
        sieve (sieve n)]
    (filter #(aget sieve %) (range 2 n))))

(defn first-n-primes [n]
  (take n (primes (nth-prime-upper-bound n))))
````
So what happen to our nifty modulo six trick? It made it into a sanity check in the test suite:
````
(deftest primegen
  (testing "Sanity check primality"
    (doseq [n (first-n-primes 50)]
      (when (> n 6)
        (let [m6 (mod n 6)]
          ;; necessary but not sufficient test for primality:
          (is (or (= m6 1)(= m6 5))))))))
````
## Stage 4: We Out
Oh, wait. They want documentation.

To grab the repo:
````bash
git clone https://github.com/kennytilton/primetimes.git
````
A pre-built binary can be found in `bin`, so you can:
````bash
cd primetimes
bin/primetimes -h
````
...to see the doc:
````bash
Functionality:

   Print a times table of the first N primes to STDOUT.

 Usage:

    primetimes [prime-ct] options*

 prime-ct: Must be at least zeo. Defaults to five (5)

 Options:
   -p, --padding CELLPADDING  2  How many extra spaces should be added to widen each cell.
  -h, --help 
````
You can rebuild the binary with:
````bash
lein bin
````
### Dependencies
* [org.clojure/clojure "1.8.0"]
* [org.clojure/tools.cli "0.3.5"] Command-line parameters for the standalone.
* [com.hypirion/primes "0.2.2"]] Testing prime generation code.
* [lein-binplus "0.6.4"] Building standalones.
