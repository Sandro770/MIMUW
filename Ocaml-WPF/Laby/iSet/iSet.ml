(*
 * PSet - Polymorphic sets
 * Copyright (C) 1996-2003 Xavier Leroy, Nicolas Cannasse, Markus Mottl
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version,
 * with the special exception on linking described in file LICENSE.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *)

(* autor: Alessandro Peria *)
(* code review: Piotr Blinowski *)

type t = 
  | Empty
  | Node of t * (int * int) * t * int * int

let height = function
  | Node (_, _, _, h, _) -> h
  | Empty -> 0

let sum a b = (* assumes that a,b >= 0 *)
  if a < 0 || b < 0 then assert(false)
  else if a = max_int || b = max_int then max_int
  else if a > max_int - b then max_int
  else a + b 

let rec cnt n (kl,kr) =
  if n < kl then 0 
  else if n > kr then cnt kr (kl, kr) 
  else if kl >= 0 then sum (n - kl) 1 
       else if n >= 0 && kl < 0 then sum (sum n (abs(kl + 1))) 2
       else cnt (n + max_int) (kl + max_int, n + max_int) 

let getLength (a,b) = cnt b (a,b);;

let getS = function 
  | Node (_, _, _, _, s) -> s
  | Empty -> 0;;

let calcSST l k r = sum (getLength k) (sum (getS l) (getS r)) (* calculate sum Subtree*) 

let make l k r = Node (l, k, r, max (height l) (height r) + 1, (calcSST l k r))

let bal l k r =
  let hl = height l in
  let hr = height r in
  if hl > hr + 2 then
    match l with
    | Node (ll, lk, lr, _, _) ->
        if height ll >= height lr then make ll lk (make lr k r)
        else
          (match lr with
          | Node (lrl, lrk, lrr, _, _) ->
              make (make ll lk lrl) lrk (make lrr k r)
          | Empty -> assert false)
    | Empty -> assert false
  else if hr > hl + 2 then
    match r with
    | Node (rl, rk, rr, _, _) ->
        if height rr >= height rl then make (make l k rl) rk rr
        else
          (match rl with
          | Node (rll, rlk, rlr, _, _) ->
              make (make l k rll) rlk (make rlr rk rr)
          | Empty -> assert false)
    | Empty -> assert false
  else Node (l, k, r, max hl hr + 1, calcSST l k r)

let rec min_elt = function
  | Node (Empty, k, _, _, _) -> k
  | Node (l, _, _, _, _) -> min_elt l
  | Empty -> raise Not_found

let rec remove_min_elt = function
  | Node (Empty, _, r, _, _) -> r
  | Node (l, k, r, _, _) -> bal (remove_min_elt l) k r
  | Empty -> invalid_arg "PSet.remove_min_elt"

let merge t1 t2 =
  match t1, t2 with
  | Empty, _ -> t2
  | _, Empty -> t1
  | _ ->
      let k = min_elt t2 in
      bal t1 k (remove_min_elt t2)

let empty = Empty;;

let is_empty x = x = empty;;
exception Foo of (int*int*int*int*string);;
let cmp (l,r) (l1,r1) = (* assumes that segments [l,r] and [l1,r1] are disjointed*)
 if l < l1 then -1
 else if l > l1 then 1
 else raise (Foo (l,r,l1,r1,"Error not disjointed")) 

let rec add_one (xl, xr) set = 
  if xl > xr then set
  else
      match set with (* assumes that there are no elements in range [xl,xr] *)
      | Node (l, (kl, kr), r, _, _) ->
          let c = cmp (xl, xr) (kl, kr) in
          if c = 0 then assert(false) 
          else if kr < xl && getLength (kr,xl) = 2(*kr + 1 = xl*) then add_one (kl,xr) (remove (kl,kr) set)
          else if xr < kl && getLength (xr,kl) = 2(*kl - 1 = xr*) then add_one (xl,kr) (remove (kl,kr) set)
          else
            if c < 0 then
              let nl = add_one (xl, xr) l in
              bal nl (kl,kr) r
            else
              let nr = add_one (xl, xr) r in
              bal l (kl,kr) nr
      | Empty -> Node (Empty, (xl,xr), Empty, 1, getLength (xl,xr))
  and 
remove (xl,xr) set = 
  let rec loop = function
    | Node (l, (actl,actr), r, h,_) ->
        let l_inter = max xl actl in
        let r_inter = min xr actr in 
        if l_inter > r_inter then 
          let c = cmp (xl,xr) (actl,actr) in
          if c < 0 then bal (loop l) (actl, actr) r 
          else bal l (actl, actr) (loop r)
        else if actl < xl && xr < actr then 
          add_one (xr+1,actr) (make l (actl, xl - 1) r)
        else if actl = l_inter && actr = r_inter then loop (merge l r) 
        else if xl <= actl && actl <= xr then loop (make l (xr+1,actr) r)
        else if xl <= actr && actr <= xr then loop (make l (actl,xl-1) r)
        else assert(false)
    | Empty -> Empty in
  loop set

let add x t =
  if fst x > snd x then (raise (Foo ((fst x), (snd x), 0, 0, "zły arg do add"))) 
  else add_one x (remove x t);;

let rec join l v r =  
  match (l, r) with
    (Empty, _) -> add_one v r
  | (_, Empty) -> add_one v l
  | (Node(ll, lv, lr, lh, _), Node(rl, rv, rr, rh, _)) ->
      if lh > rh + 2 then bal ll lv (join lr v r) else
      if rh > lh + 2 then bal (join l v rl) rv rr else
      make l v r

let split x set =
  let rec loop x = function
      Empty ->
        (Empty, false, Empty)
    | Node (l, (vl,vr), r, _, _) ->
        if vl <= x && x <= vr then (add_one (vl,x-1) l, true, add_one (x + 1,vr) r)
        else
          let c = cmp (x,x) (vl,vr) in
          if c < 0 then
            let (ll, pres, rl) = loop x l in (ll, pres, join rl (vl,vr) r)
          else
            let (lr, pres, rr) = loop x r in (join l (vl,vr) lr, pres, rr)
  in
  let setl, pres, setr = loop x set in
  setl, pres, setr

let mem x set =
  let rec loop = function
    | Node (l, (kl,kr), r, _, _) ->
        let c = 
          if kl <= x && x <= kr then 0
          else cmp (x,x) (kl,kr) in
        c = 0 || loop (if c < 0 then l else r)
    | Empty -> false in
  loop set

let exists = mem

let iter f set =
  let rec loop = function
    | Empty -> ()
    | Node (l, k, r, _, _) -> loop l; f k; loop r in
  loop set

let fold f set acc =
  let rec loop acc = function
    | Empty -> acc
    | Node (l, k, r, _, _) ->
          loop (f k (loop acc l)) r in
  loop acc set

let elements set = 
  let rec loop acc = function
      Empty -> acc
    | Node(l, k, r, _, _) -> loop (k :: loop acc r) l in
  loop [] set

let below n set = 
  let rec loop = function
    | Empty -> 0
    | Node(l, (kl, kr), r, _, _) -> let toAdd = cnt n (kl,kr) in
                          sum toAdd (if n < kl then loop l else sum (getS l) (loop r)) in
  loop set     


