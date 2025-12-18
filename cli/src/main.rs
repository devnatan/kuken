use clap::Parser;

mod cli;

#[tokio::main]
async fn main() {
    let cli = cli::Cli::parse();

}