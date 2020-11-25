export async function startGame() {
  const response = await fetch("/start");
  if (response.ok) {
    return await response.json();
  }
}

export async function playMove(player, pit) {
  const response = await fetch(`/move?playerID=${player}&pitID=${pit}`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
  });

  if (response.ok) {
    return await response.json();
  }
}
