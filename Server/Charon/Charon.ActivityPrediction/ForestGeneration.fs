namespace Charon.ActivityPrediction

open System
open System.IO
open Charon
open Nessos.FsPickler

module Path = 
    let appDir = AppDomain.CurrentDomain.SetupInformation.ApplicationBase
    let makeAppRelative fileName = System.IO.Path.Combine(appDir, fileName)

module ForestGeneration =

    type activity = {
        dob: int64;
        height: double;
        weight: double;
        duration: double;
        distance: double;
        steps: int;
        act_type: string;
    }

    let readDataset (filename : string) =
        let file = File.ReadAllLines(Path.makeAppRelative filename)

        let data = 
            file
            |> Array.map (fun line -> line.Split(','))
            |> Array.filter (fun line -> Array.length line = 10)
            |> Array.map (fun line ->
                line.[9],
                { dob = DateTime.Parse(line.[3]).Ticks;
                  height = double line.[4];
                  weight = double line.[5];
                  duration = double line.[6];
                  distance = double line.[7];
                  steps = int line.[8];
                  act_type = line.[9]; })

        data

    let createForest () =
        let data = readDataset("""activity.csv""")

        let labels = "Type", (fun (x:string) -> Some(x)) |> Categorical

        let features =
            [ ("dob", (fun x -> x.dob |> Some) |> Numerical);
              ("height", (fun x -> x.height |> Some) |> Numerical);
              ("weight", (fun x -> x.weight |> Some) |> Numerical);
              ("duration", (fun x -> x.duration |> Some) |> Numerical);
              ("distance", (fun x -> x.distance |> Some) |> Numerical);
              ("steps", (fun x -> x.steps |> Some) |> Numerical); ]

        let forest = forest data (labels, features) DefaultSettings

        let binary = FsPickler.CreateBinary()
        let byte_stream = binary.Pickle forest
        File.WriteAllBytes(Path.makeAppRelative """forest.bin""", byte_stream)
