(*      author: Alessandro Peria *)
(* code review: Michal Plachta   *)
exception Cykliczne

open PMap;;

let topol lista = 
  if lista = [] then [] else
    
  let sz = ref 1 in
  let firstEl = fst (List.hd lista) in
  
  let mapa = 
    let f am e = 
      if exists e am then am 
      else 
        begin 
          sz := !sz + 1;
          (add e (!sz-1) am)
        end  
    in

    let g am l = List.fold_left (fun am e -> (f am e)) am l in
   
    List.fold_left (fun am (e,l) -> (g (f am e) l) ) (add firstEl 0 empty) lista
  in
  
  
  let nbToEl = Array.make !sz (fst (List.hd lista)) in
  iter (fun key value -> nbToEl.(value) <- key) mapa;

  let graph = Array.make !sz [] in  
  let odw = Array.make !sz false in
  
  let getNbrs l = 
    List.fold_left (fun a e -> (find e mapa)::a) [] l in

  let buildGraph = 
    List.iter (fun (e, lnei) -> 
      let nb = find e mapa in
      graph.(nb) <- ((getNbrs lnei) @ graph.(nb))) lista
  in
  buildGraph;

  let ans = ref [] in
  let ansByNbrs = ref [] in 
  
  let rec dfs v = 
    begin
    odw.(v) <- true;
    List.iter (fun nei -> if odw.(nei) = false then (dfs nei) else ()) (graph.(v));
    ans := nbToEl.(v)::(!ans);
    ansByNbrs := v::(!ansByNbrs)
    end
  in

  for i = 0 to !sz - 1 do
    if odw.(i) = false then dfs(i);
  done;
  
  let posOnTopo = Array.make !sz (-1) in

  let rec srt l cnt =
    if l = [] then ()
    else 
      begin
        posOnTopo.(List.hd l) <- cnt; 
        srt (List.tl l) (cnt + 1)
      end in
  srt (!ansByNbrs) 0;
  
  for i = 0 to !sz - 1 do 
      List.iter (fun e -> if posOnTopo.(i)  < posOnTopo.(e) then () else raise Cykliczne) (graph.(i))     
  done;

  !ans;;

