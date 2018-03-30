# primetimes
Hmm. A coding challenge involves determining the first N primes, without using a certain library that would help with that. Fine, I am at heart a Common Lisper, I love ignoring Java, the Gosling/Steele un-contrib that had The Great Unwashed driving with the brakes on until Rich Hickey begat The Second Coming of John Mccarthy's Lisp.

But this is for an established financial institution of some repute and having been at this IT game for as many decades as the years of experience required by many job listings, I know fast prime generation is a useful assessment of how well I can help them, so my first win on the challenge is to challenge Management's spec. I am engineer, I have an obligation to productivity. 

Google, here I come.

## Stage 1: Planning
We think through the technology, where to improve the requirements, and where to go all Dukes of Hazzard on management roadblocks.

### Cheating on prime numbers

Searching for "clojure prime numbers" I quickly find [The Forbidden Fruit](https://github.com/hypirion/primes). Clojure is good that way, libs for everything. I add `[com.hypirion/primes "0.2.2"]` to my project dependencies.

But I like to keep an open mind -- maybe this prospective employer knows what they are doing -- and grab also the prime number code from some [cattle rustler](http://www.thesoftwaresimpleton.com/blog/2015/02/07/primes/). Ha-ha, challenge! You tried to make me write a prime number generator, but there is more than one way to skin a code theft! 

Paul the rustler done good, beats hypirion by about an order of magnitude to the millionth prime. But then I notice the fine print. Not only must I eschew The Forbidden Fruit, I must: *(write your own code)*. My prospective employer is losing points fast. Must I also leave out anything I have learned about programming before doing the challenge? If I took an algorithm's class must I avoid any techniques learned? 

Mind you, I have them on a technicality: if I were writing my own code I would go on Google, find Paul's hack and go with it. But along the way I was surprised by [this insight](https://primes.utm.edu/notes/faq/six.html), so I will likely come back to this to see how well I can do with that. Sadly, I do not see how to leverage the divide by six insight in combination with the sieve, so in the end I will prolly code my own to make the boss happy.

For now I will use Rustler's Primes and get on with the crux of the project.
>
> The challenge, good for it, has flushed me out and revealed a second inclination of mine along
> with helping management by gently ignoring them: Start with the crux. Manage risk 
> by moving the interesting bit earlier. Fail fast, as they say.
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
  |  2  3  5
__|_________
2 |  4  6 10
3 |  6  9 15
5 | 10 15 25
````
Will they even notice? I wager not. Instead, they will be outraged that I cheated on formatting by calling on my Common Lisp training to use [`clojure.pretty-print/cl-format`](https://clojuredocs.org/clojure.pprint/cl-format), a nifty Clojure implementation of Common Lisp [format](http://www.lispworks.com/documentation/lw50/CLHS/Body/f_format.htm). Ha-ha, they did not think to forbid that quirky, not-so-little text formatting DSL! From memory:

````lisp
(pp/cl-format s "~vd | ~{~vd~}" row-no-width multiplier product-width products)
````
Just have to remind myself of how to right-justify, check my use of ~vm, check my width qualifier syntax -- the rule is, if you can code Lisp format from memory you need to go do something else for a while.

The other trick I will throw in is good old core.async, feeding the first row of primes to a processor that will multiply which will use another channel to feed the products to a printer function. Might even see if I can sneak in a transducer and ask for another $10k.

### Insubordination on testing
The spec requires TDD. There again the challenge will be challenged. Do I need tests *during development*? Probably not, though I can write some up *after* development to make refactoring safer. We will see, but the instructions should have been "Use TDD where and how you deem best, explaining your choices."

### Compliance. Finally.
The instructions were accompanied by this injunction: "[include] a bit of a narrative of why you did what you did.
". Hence this. And narrative it will be: the above will be retained mostly as is, with new narrative as we work.

## First Contact
RSN.

