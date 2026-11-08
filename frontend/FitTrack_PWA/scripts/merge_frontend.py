#!/usr/bin/env python3
"""Safely overlay FitTrack PWA v2 into an existing fittrack-pwa folder.

Defaults to a DRY RUN. --apply backs up the previous frontend first.
No Java backend code, passwords, .env, git metadata or node_modules are overwritten.
"""

from __future__ import annotations

import argparse
from datetime import datetime
from pathlib import Path
import shutil
import sys
import zipfile

HERE = Path(__file__).resolve()
SOURCE = HERE.parent.parent
IGNORE_DIRS = {"node_modules", "dist", ".git", ".fittrack-backups", "__pycache__", ".vite"}
IGNORE_FILES = {".env", ".env.local", ".env.production", ".env.development", "package-lock.json", "pnpm-lock.yaml", "yarn.lock"}


def collect_files(root: Path):
    for item in sorted(root.rglob("*")):
        if not item.is_file():
            continue
        relative = item.relative_to(root)
        if any(segment in IGNORE_DIRS for segment in relative.parts):
            continue
        if item.name in IGNORE_FILES:
            continue
        yield item, relative


def find_project_root(frontend: Path) -> Path:
    """Support both FitTrack/fittrack-pwa and FitTrack/frontend/fittrack-pwa."""
    candidates = [frontend.parent, frontend.parent.parent]
    for candidate in candidates:
        if any((candidate / rel).is_file() for rel in (
            "beckend/fittrack/gradlew",
            "backend/fittrack/gradlew",
            "fittrack/gradlew",
        )):
            return candidate
    return frontend.parent


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--target",
        type=Path,
        default=SOURCE.parent / "fittrack-pwa",
        help="Поточна папка Frontend (за замовчуванням ../fittrack-pwa)",
    )
    parser.add_argument("--apply", action="store_true", help="Реально скопіювати файли після backup")
    parser.add_argument(
        "--with-launcher",
        action="store_true",
        help="Також встановити start-fittrack.sh у корені проєкту",
    )
    args = parser.parse_args()

    target = args.target.expanduser().resolve()
    project_root = find_project_root(target)
    if target == SOURCE or SOURCE in target.parents or target in SOURCE.parents:
        print("ПОМИЛКА: ціль не повинна бути вихідною папкою або її батьком/нащадком.")
        return 2

    src_files = list(collect_files(SOURCE))
    changed = []
    unchanged = 0
    for src, relative in src_files:
        dest = target / relative
        if dest.is_file() and src.read_bytes() == dest.read_bytes():
            unchanged += 1
        else:
            changed.append((src, dest, relative))

    print(f"Нова версія: {SOURCE}")
    print(f"Поточний Frontend: {target}")
    print(f"До копіювання: {len(changed)} файлів; без змін: {unchanged}")
    print("Не чіпаємо: Java Backend, .env, node_modules, dist, .git, lock-файли")
    for _, destination, relative in changed:
        state = "REPLACE" if destination.exists() else "NEW"
        print(f"  {state:7} {relative}")

    if not args.apply:
        print("\nЦе лише перегляд. Для реального об’єднання додай --apply")
        return 0

    target.mkdir(parents=True, exist_ok=True)
    date = datetime.now().strftime("%Y%m%d-%H%M%S")
    backup_dir = target.parent / "fittrack-frontend-backups"
    backup_dir.mkdir(parents=True, exist_ok=True)
    backup_path = backup_dir / f"frontend-before-v2-{date}.zip"
    with zipfile.ZipFile(backup_path, "w", zipfile.ZIP_DEFLATED) as archive:
        if target.exists():
            for existing, relative in collect_files(target):
                archive.write(existing, arcname=str(relative))
            # Keep deployment settings backed up locally only; never copy them
            # from the new ZIP's source tree.
            for env_name in (".env", ".env.local"):
                config = target / env_name
                if config.is_file():
                    archive.write(config, arcname=env_name)
        if args.with_launcher:
            existing_launcher = project_root / "start-fittrack.sh"
            if existing_launcher.is_file():
                archive.write(existing_launcher, arcname="_project_root/start-fittrack.sh")

    print(f"\nРезервна копія: {backup_path}")
    for src, dest, _ in changed:
        dest.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(src, dest)

    env = target / ".env"
    if not env.exists():
        example = target / ".env.example"
        if example.is_file():
            contents = example.read_text(encoding="utf-8")
            contents = contents.replace("VITE_DEMO_MODE=true", "VITE_DEMO_MODE=false")
            env.write_text(contents, encoding="utf-8")
            print("Створено .env із VITE_DEMO_MODE=false")

    if args.with_launcher:
        source_launcher = SOURCE / "scripts" / "start-fittrack.sh"
        destination = project_root / "start-fittrack.sh"
        shutil.copy2(source_launcher, destination)
        destination.chmod(0o755)
        print(f"Лаунчер встановлено: {destination}")

    print("\nОб’єднання завершено. Зайди в папку Frontend, запусти npm install, npm run dev.")
    print("Якщо старі зміни потрібні — backup ZIP дає змогу їх відновити.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
