namespace Charon.ActivityPrediction

open System.IO
open Charon
open Nessos.FsPickler
open Charon.ActivityPrediction.ForestGeneration

module Predictor =

    let classify (a : activity) =
        try
            let file = File.ReadAllBytes(Path.makeAppRelative """forest.bin""")
            let binary = FsPickler.CreateBinary()
            let forest = binary.UnPickle<ForestResults<activity>> file

            //re-write file to help prevent corruption
            let byte_stream = binary.Pickle forest
            File.WriteAllBytes(Path.makeAppRelative """forest.bin""", byte_stream)


            let decision = forest.Classifier a
            decision
        with
        | _ -> 
            let decision = "O" 
            decision

    let predict (array : ResizeArray<string>) =
        
        let a = { dob = int64 array.[0]; 
                  height = double array.[1]; 
                  weight = double array.[2]; 
                  duration = double array.[3]; 
                  distance = double array.[4]; 
                  steps = int array.[5]; 
                  act_type = "" }
        let decision = classify(a)
        decision        

    let testAccuracy () =
        let data = ForestGeneration.readDataset("""test.csv""")

        let quality =
            data
            |> Seq.averageBy (fun (t,a) ->
                if (System.String.Equals(a.act_type, classify(a))) then 1. else 0.)
        quality
