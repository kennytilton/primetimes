# primetimes
Hmm. A coding challenge involves determining the first N primes, without using a certain library that would help with that. Fine, I am at heart a Common Lisper, I love ignoring Java, the Gosling/Steele un-contrib that had The Great Unwashed driving with the brakes on until Rich Hickey begat The Second Coming of John Mccarthy's Lisp.

But this is for an established financial institution of some repute and having been at this IT game for as many decades as the years of experience required by many job listings, I know fast prime generation is not a useful assessment of how well I can help them, so my first win on the challenge is to challenge Management's spec. 

I am engineer, I have an obligation to productivity. Google, here I come.

## Stage 1: Planning
We think through the technology, where to improve the requirements, and where to go all Dukes of Hazzard on management roadblocks.

### Cheating on prime numbers

Searching for "clojure prime numbers" I quickly find [The Forbidden Fruit](https://github.com/hypirion/primes). Clojure is good that way, libs for everything. I add `[com.hypirion/primes "0.2.2"]` to my project dependencies.

But I like to keep an open mind -- maybe this prospective employer knows what they are doing -- and grab also the prime number code from some [cattle rustler](http://www.thesoftwaresimpleton.com/blog/2015/02/07/primes/). Ha-ha, challenge! You tried to make me write a prime number generator, but there is more than one way to skin a code theft! 

Paul the rustler done good, beats hypirion by about an order of magnitude to the millionth prime. But then I notice the fine print. Not only must I eschew The Forbidden Fruit, I must: *(write your own code)*. My prospective employer is losing points fast. Must I also leave out anything I have learned about programming before doing the challenge? If I took an algorithm's class must I avoid any techniques learned? 

Mind you, I have them on a technicality: if I were writing my own code I would go on Google, find Paul's hack and go with it. But along the way I was surprised by [this insight](https://primes.utm.edu/notes/faq/six.html), so I will likely come back to this to see how well I can do with that. Sadly, I do not see how to leverage the divide by six insight in combination with the sieve, so in the end I will prolly code my own to make the boss happy.

For now I will use Rustler's Primes and get on with the crux of the project.
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
I wonder if they will be concerned that I cheated on formatting by calling on my Common Lisp training to use [`clojure.pretty-print/cl-format`](https://clojuredocs.org/clojure.pprint/cl-format), a nifty Clojure implementation of Common Lisp [format](http://www.lispworks.com/documentation/lw50/CLHS/Body/f_format.htm). 

From memory:

````lisp
(pp/cl-format s "~vd | ~{~vd~}" row-no-width row-no product-width products)
````
Just have to remind myself of how to right-justify, check my use of ~vm, check my width qualifier syntax -- the rule is, if you can code Lisp format from memory you need to go do something else for a while.

### Insubordination on testing
The spec requires TDD. There again the challenge will be challenged. Do I need tests *during development*? Probably not, though I can write some up *after* development to make refactoring safer. We will see, but the instructions might have been "Use TDD where and how you deem best, explaining your choices."

### Compliance. Finally.
The instructions were accompanied by this injunction: "[include] a bit of a narrative of why you did what you did.
". Hence this. And narrative it will be: the above will be retained mostly as is, with new narrative as we work.

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
And now life is good. Displaying just a couple of rows:
````clojure
(doseq [p [3 5]]
  (println (row-fmt 4 p [2 3 5 7 11 13])))

=>
   3 |    2   3   5   7  11  13 |
   5 |    2   3   5   7  11  13 |
````
Wrapped with some more code to handle the multiplication and some nice ASCII tabling:
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
But along the way I had to solve some easy-to-reintroduce bugs that messed up the layout, so we will toss in a test at the end to support refactoring.
>
> Inclination #3: Defer drudgery. Keep the fun going. Tedious stuff is more bearable when the prize is in hand.
>

Time to build a standalone accepting command-line parameters, using my old stand-bys clojure [tools.cli](https://github.com/clojure/tools.cli) and [lein bin-plus](https://github.com/BrunoBonacci/lein-binplus).
## Prime numbers stand alone
Voila!
![Times table for First 15 Primes](https://github.com/kennytilton/primetimes/blob/master/doc/table-15.jpg)

Now just for the fun of it let us write our own prime number generator and make the boss happy.

### My very own prime generator
Ignoring Mr. Cowan's work is tricky. We will try reproducing his work from scratch.

Contemplation of the Sieve of Eratosthenes makes clear we need to know how far to go when propagating out the multiples of identified primes. Mr. Cowan recommends Wikipedia, but my best find was [on tack Overflow](https://stackoverflow.com/questions/9625663/calculating-and-printing-the-nth-prime-number):
````
n*(log n + log (log n) - 1) < p(n) < n*(log n + log (log n)), for n >= 6.
````
Ah, I was wondering why Paul threw in an airbag `...+ 3`. That was a clever, un-self-documenting, negligibly wasteful way of avoiding a conditional. Me, I do not like confuding my reader, we will use the formula only when `n >= 6`.
````
