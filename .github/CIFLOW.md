# CI/CD Workflow and Branch Strategy

This document describes the Git branching model and GitHub Actions CI/CD pipelines used by epsilon-runtime. The project follows a GitFlow-based workflow with automated builds, releases, and deployments.

## Branch Strategy

The project uses [GitFlow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow) for branch management:

```mermaid
gitGraph
    commit id: "initial"
    branch develop
    checkout develop
    commit id: "dev work"
    branch feature/JNG-1
    commit id: "feature"
    checkout develop
    merge feature/JNG-1 id: "merge feature"
    branch release/1.0-beta1
    commit id: "stabilize"
    checkout main
    merge release/1.0-beta1 id: "release 1.0"
    checkout develop
    merge release/1.0-beta1 id: "back-merge"
    commit id: "continue dev"
```

| Branch Pattern | Base | Purpose |
|---------------|------|---------|
| `develop` | — | Main development branch; contains latest sources of the active version |
| `feature/JNG-XXX_summary` | `develop` | New features for the next release |
| `release/X.Y-betaN` or `X_Y_betaN` | `develop` | Release stabilization branches |
| `bugfix/JNG-XXX_summary` | `release/*` | Bug fixes during release testing (must be applied to newer versions too) |
| `support/JNG-XXX_summary` | `release/*` | Minor changes to a previous release |
| `master` | — | Latest released production sources |
| `hotfix/JNG-XXX_summary` | `master` | Critical fixes applied to both `master` and `develop` |

## Version Numbering

Versions follow semantic versioning with these rules:

1. **Feature branches** — do not change version numbers
2. **Release branch creation** — increment the 2nd number on `develop`
3. **Bugfix branches** — do not change version numbers (they target release branches before merging to master)
4. **Support branches** — increment the 3rd number when started
5. **Hotfix branches** — increment the 4th number when started; applied to both release and master

## GitHub Actions Pipelines

### Build Pipeline (`build.yml`)

Triggered on every push to `develop` and on pull requests targeting `develop`, `master`, `increment/*`, or `release/*`.

```mermaid
flowchart TD
    TRIGGER["Push to develop<br/>or PR to develop/master/increment/release"]
    TRIGGER --> CHECK{Base branch?}

    CHECK -->|master, release/*| VER_RELEASE["Set version from pom.xml<br/>(without -SNAPSHOT)"]
    CHECK -->|develop, increment/*| VER_DEV["Set version:<br/>major.minor.qualifier.date_commitId_branch"]

    VER_RELEASE --> BUILD["Build & deploy to Nexus"]
    VER_DEV --> BUILD

    BUILD --> TAG["Create git tag v&lt;version&gt;"]
    TAG --> IS_RELEASE{increment/* or release/*?}

    IS_RELEASE -->|Yes| MERGE_TAG["Create tag merge-pr/&lt;version&gt;"]
    MERGE_TAG --> MERGE_TRIGGER["Triggers merge-pr-tagged.yml"]

    IS_RELEASE -->|No| IS_DEVELOP{develop?}
    IS_DEVELOP -->|Yes| CHANGELOG["Build changelog"]
    CHANGELOG --> GH_RELEASE["Create GitHub prerelease"]
```

### Merge PR Pipeline (`merge-pr-tagged.yml`)

Triggered when a `merge-pr/*` tag is pushed. Determines whether to merge to `master` (for releases) or squash to `develop` (for increments):

```mermaid
flowchart TD
    TRIGGER["Push on merge-pr/* tag"]
    TRIGGER --> EXTRACT["Extract version from tag"]
    EXTRACT --> CHECK{Version format?}

    CHECK -->|major.minor.qualifier| MASTER["Merge PR to master"]
    MASTER --> RELEASE_TRIGGER["Triggers create-release-on-master.yml"]

    CHECK -->|other format| DEVELOP["Squash PR to develop"]
    DEVELOP --> BUILD_TRIGGER["Triggers build.yml"]

    MASTER --> CLEANUP["Delete merge-pr tag"]
    DEVELOP --> CLEANUP
```

### Release on Master (`create-release-on-master.yml`)

Triggered on push to `master`. Builds a changelog and creates a GitHub release (marked as latest).

### Manual Release (`release.yml`)

Manually triggered with a version parameter (or `auto` to use the pom.xml version):

```mermaid
flowchart TD
    TRIGGER["Manual trigger with version"]
    TRIGGER --> CHECK{Version = 'auto'?}

    CHECK -->|Yes| POM["Read version from pom.xml<br/>(strip -SNAPSHOT)"]
    CHECK -->|No| GIVEN["Use given version"]

    POM --> NEXT["Calculate next version<br/>(qualifier + 1)"]
    GIVEN --> NEXT

    NEXT --> PR_MASTER["Create PR to master<br/>with release version"]
    NEXT --> PR_DEVELOP["Create PR to develop<br/>with next version"]

    PR_MASTER --> BUILD1["Triggers build.yml"]
    PR_DEVELOP --> BUILD2["Triggers build.yml"]
```

### Other Workflows

| Workflow | Purpose |
|----------|---------|
| `bump-version.yml` | Bumps project version |
| `build-dependabot.yml` | Builds Dependabot PRs |
| `delete-old-draft-releases.yml` | Cleans up old draft releases |
| `jira-description-to-pr.yml` | Copies JIRA ticket descriptions into PR bodies |
| `sync-labels.yml` | Synchronizes GitHub labels |

## Development Rules

> **Important:** There is no commit without a ticket number. Every commit and pull request must reference a JIRA ticket in `JNG-XXX` format.

Issue tracking: [JIRA Dashboard](https://blackbelt.atlassian.net/jira/dashboards)
